package com.gongshijia.mms.core

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.LogEntry
import akka.util.ByteString
import org.mongodb.scala.bson.ObjectId
import redis.ByteStringFormatter
import spray.json.{DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by hary on 2017/5/12.
  */
trait HttpSupport extends Core with ExceptionSupport with ApiSupport with SprayJsonSupport with RedisSupport {

  import akka.http.scaladsl.server.Directive1
  import akka.http.scaladsl.server.Directives._

  implicit val byteStringFormatter = new ByteStringFormatter[Session] {
    override def serialize(data: Session): ByteString = {
      ByteString(s"${data.session_key}")
    }

    override def deserialize(bs: ByteString): Session = {
      val ss = bs.utf8String
      Session(ss)
    }
  }


  // 指令
  val openid: Directive1[String] = headerValueByName("X-OPENID")

  def optSession(openid: String): Directive1[Future[Option[Session]]] = provide(redisClient.get(openid))


  // 一些常用的spray-json format
  implicit object SqlTimestampFormat extends RootJsonFormat[Timestamp] {
    override def write(obj: Timestamp) = {
      val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      JsString(formatter.format(obj))
    }

    override def read(json: JsValue): Timestamp = {
      val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      json match {
        case JsString(s) => new Timestamp(formatter.parse(s).getTime)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
  }

  implicit object DateJsonFormat extends RootJsonFormat[Date] {

    val formatter = new SimpleDateFormat("yyyyMMdd")

    override def write(obj: Date) = JsString(formatter.format(obj))

    override def read(json: JsValue): Date = json match {
      case JsString(s) => formatter.parse(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }

  implicit object ObjectIdJsonFormat extends RootJsonFormat[ObjectId] {

    override def write(obj: ObjectId) = JsString(obj.toString)

    override def read(json: JsValue): ObjectId = json match {
      case JsString(s) => new ObjectId(s)
      case _ => throw new DeserializationException("Error info you want here ...")
    }
  }


  // 启动http
  def startHttp(route: Route) = {

    def extractLogEntry(req: HttpRequest): RouteResult => Option[LogEntry] = {
      case RouteResult.Complete(res) => Some(LogEntry(req.method.name + " " + req.uri + " => " + res.status, Logging.InfoLevel))
      case _ => None // no log entries for rejections
    }

    val wrapper: Route = logRequestResult(extractLogEntry _)(route)

    log.info(s"http is listening on ${coreConfig.getInt("http.port")}")
    Http().bindAndHandle(wrapper, "0.0.0.0", coreConfig.getInt("http.port"))
  }


  private def completeReject(uri: Uri, status: StatusCode, msg: String) = {
    log.warning(s"{} failed, error: {}", uri, msg)
    complete(HttpResponse(status, entity = failed(status.intValue, msg).toJson.toString()))
  }

  private def completeException(uri: Uri, status: StatusCode, e: InternalException) = {
    log.warning(s"{} failed, error: {}", uri, e.message)
    log.debug("{}", e.getStackTrace.mkString("\n"))
    complete(HttpResponse(status, entity = failed(status.intValue, e.getStackTrace.mkString("\n")).toJson.toString()))
  }

  // akka-http exception handler
  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: InternalException =>
      extractUri { uri => completeException(uri, StatusCodes.InternalServerError, e) }
    case NonFatal(e) =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!!")
        log.error("{}", e.getStackTrace.mkString("\n"))
        complete(HttpResponse(StatusCodes.InternalServerError, entity = failed(500, e.getStackTrace.mkString("\n")).toJson.toString()))
      }
  }

  // akka-http rejection handler
  implicit def myRejectionHandler =
    RejectionHandler.newBuilder().handle {
      case MissingCookieRejection(cookieName) => extractUri { uri => completeReject(uri, BadRequest, "cookie needed") }
      case MissingQueryParamRejection(name) => extractUri { uri => completeReject(uri, BadRequest, s"missing parameter $name") }
      case AuthorizationFailedRejection => extractUri { uri => completeReject(uri, BadRequest, s"authorization failed") }
      case ValidationRejection(msg, _) => extractUri { uri => completeReject(uri, BadRequest, s"validation: $msg") }
      case MissingHeaderRejection(header) => extractUri { uri => completeReject(uri, BadRequest, s"missing $header") }
      case MalformedRequestContentRejection(msg, _) => extractUri { uri => completeReject(uri, BadRequest, msg)}
    }.handleAll[MethodRejection] { methodRejections =>
      extractUri { uri =>
        val names = methodRejections.map(_.supported.name).mkString(",")
        completeReject(uri, BadRequest, s"only support method: $names")
      }
    }.handleNotFound {
      extractUri { uri => completeReject(uri, BadRequest, s"not found") }
    }.result()
}
