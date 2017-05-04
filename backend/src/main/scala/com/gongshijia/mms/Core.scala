package com.gongshijia.mms

import java.sql.Timestamp
import java.text.SimpleDateFormat

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Forbidden, InternalServerError, MethodNotAllowed}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives.{complete, extractUri}
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import com.gongshijia.mms.mmsApp.coreSystem
import com.gongshijia.mms.service.{MMSService, UserMaster}
import redis.{ByteStringFormatter, RedisClient}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import scala.concurrent.Future
import scala.util.control.NonFatal

/**
  * Created by hary on 17/1/9.
  */
trait Core extends MMSService with DefaultJsonProtocol {

  // 系统， 配置， 日志
  implicit val coreSystem: ActorSystem = mkSystem
  implicit val coreMaterializer = ActorMaterializer()
  val coreConfig = coreSystem.settings.config
  val log = Logging(coreSystem, this.getClass)
  protected def mkSystem: ActorSystem = ActorSystem("mms-system")

  // redis
  val redis = RedisClient(coreConfig.getString("redis.host"), coreConfig.getInt("redis.port"))

  // neo4j
  // val neo4j =

  // mongo
  // val mongo

  // 扩展指令
  case class WxSession(openid: String, session_key: String, expires_in: Long)
  implicit val WxSessionFormat = jsonFormat3(WxSession)
  implicit val byteStringFormatter = new ByteStringFormatter[WxSession] {
    override def serialize(data: WxSession): ByteString = {
      ByteString(s"${data.openid}|${data.session_key}|${data.expires_in}")
    }
    override def deserialize(bs: ByteString): WxSession = {
      val ss = bs.utf8String.split("\\|")
      println()
      WxSession(ss(0), ss(1), ss(2).toLong)
    }
  }
  import akka.http.scaladsl.server.Directives._
  val wxSession: Directive1[Future[Option[WxSession]]] = optionalHeaderValueByName("X-SID") flatMap {
    case Some(sid) => provide(redis.get[WxSession](sid))
    case None =>  provide(Future.successful(None))
  }
  // API接口定义
  case class Pagination(total: Int, pageSize: Int, curPage: Int)
  case class Error(code: Int, message: String)
  case class Result[T](data: Option[T], error: Option[Error] = None, pagination: Option[Pagination] = None)
  implicit val PaginationFormat = jsonFormat3(Pagination)
  implicit val ErrorFormat = jsonFormat2(Error)
  implicit def resultFormat[A: JsonFormat] = jsonFormat3(Result.apply[A])
  def failed(code: Int, message: String) = Result[Int](None, Some(Error(code, message)))

  // 异常定义
  case class DatabaseException(message:String) extends Exception
  case class ParameterException(message: String) extends Exception
  case class BusinessException(message: String) extends Exception

  // 一些常用的spray-json format
  implicit object SqlTimestampFormat extends RootJsonFormat[Timestamp] {
    override def write(obj: Timestamp) = {
      val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      JsString(formatter.format(obj))
    }
    override def read(json: JsValue) : Timestamp = {
      val formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
      json match {
        case JsString(s) => new Timestamp(formatter.parse(s).getTime)
        case _ => throw new DeserializationException("Error info you want here ...")
      }
    }
  }

  // akka-http exception handler
  implicit def myExceptionHandler: ExceptionHandler = ExceptionHandler {
    case e: BusinessException =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!! BusinessException")
        complete(
          HttpResponse(StatusCodes.BadRequest, entity = Result(data = Some("error"), error = Some(Error(409, e.message))).toJson.toString))
      }

    case e: DatabaseException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally!!!!!!!!! DatabaseException11111111 {}", e.message)
        complete(HttpResponse(StatusCodes.BadRequest, entity = e.message))
      }
    case NonFatal(e) =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!!")
        log.error("{}", e)
        complete(HttpResponse(StatusCodes.InternalServerError, entity = Result(data = Some("error"), error = Some(Error(500, "系统错误"))).toJson.toString))
      }
  }

  // akka-http rejection handler
  implicit def myRejectionHandler =
    RejectionHandler.newBuilder()
      .handle { case MissingCookieRejection(cookieName) =>
        extractUri{ uri =>
          log.warning(s"$uri No cookies, no service!!!")
          complete(HttpResponse(BadRequest, entity = "No cookies, no service!!!"))
        }
      }
      .handle { case MissingQueryParamRejection(name) =>
        extractUri { uri =>
          log.warning(s"$uri miss request parameter $name")
          complete((Forbidden, s"miss request parameter $name"))
        }
      }
      .handle { case AuthorizationFailedRejection =>
        extractUri { uri =>
          log.warning(s"$uri You're out of your depth!")
          complete((Forbidden, "You're out of your depth!"))
        }
      }
      .handle { case ValidationRejection(msg, _) =>
        extractUri { uri =>
          log.warning(s"$uri That wasn't valid! {}",msg)
          complete((InternalServerError, "That wasn't valid! " + msg))
        }
      }
      .handleAll[MethodRejection] { methodRejections =>
      extractUri { uri =>
        val names = methodRejections.map(_.supported.name)
        log.warning("{} Can't do that! Supported: {} ",uri,names)
        complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
    }
      .handleNotFound {
        complete(HttpResponse(StatusCodes.NotFound, entity = "Not found"))
      }
      .result()
}


