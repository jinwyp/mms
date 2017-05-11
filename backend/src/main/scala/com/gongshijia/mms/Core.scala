package com.gongshijia.mms

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, Forbidden, InternalServerError, MethodNotAllowed}
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.server._
import akka.stream.ActorMaterializer
import akka.util.ByteString
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.{Document, MongoClient, Observable}
import redis.{ByteStringFormatter, RedisClient}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

import scala.concurrent.duration.{Duration, _}
import scala.concurrent.{Await, Future}
import scala.util.control.NonFatal

/**
  * Created by hary on 17/1/9.
  */
trait Core extends DefaultJsonProtocol {

  // 系统， 配置， 日志
  implicit val coreSystem: ActorSystem = mkSystem
  implicit val coreMaterializer = ActorMaterializer()

  // 线程池
  implicit val ec = coreSystem.dispatcher

  // 配置
  val coreConfig = coreSystem.settings.config
  val accessKeyId = coreConfig.getString("aliyun.accessKeyId")
  val accessKeySecret = coreConfig.getString("aliyun.accessKeySecret")
  val ossBucket = coreConfig.getString("aliyun.ossBucket")
  val ossHost = coreConfig.getString("aliyun.ossHost")
  val domain = coreConfig.getString("mms.domain")

  // 日志
  val log = Logging(coreSystem, this.getClass)

  protected def mkSystem: ActorSystem = ActorSystem("mms-system")

  val appId = coreConfig.getString("wx.appId")
  val appSecret = coreConfig.getString("wx.appSecret")

  val mongoUri = coreConfig.getString("mongo.uri")

  // redis
  val redis = RedisClient(coreConfig.getString("redis.host"), coreConfig.getInt("redis.port"))

  // neo4j
  // val neo4j =

  // mongo
  val mongoClient = MongoClient(mongoUri);

  // 扩展指令
  case class Session(session_key: String)

  case class WxSession(openid: String, session_key: String, expires_in: Long)

  implicit val WxSessionFormat = jsonFormat3(WxSession)
  implicit val byteStringFormatter = new ByteStringFormatter[Session] {
    override def serialize(data: Session): ByteString = {
      ByteString(s"${data.session_key}")
    }

    override def deserialize(bs: ByteString): Session = {
      val ss = bs.utf8String
      Session(ss)
    }
  }

  import akka.http.scaladsl.server.Directives._

  case class LoginRequest(
                           code: String,
                           avatarUrl: String,
                           country: String,
                           province: String,
                           city: String,
                           gender: Int,
                           language: String,
                           nickName: String,
                           rawData: String,
                           signature: String,
                           encryptedData: String,
                           iv: String
                         )

  implicit val LoginRequestFormat = jsonFormat12(LoginRequest)

  // 获取WxSession
  def createSession(request: LoginRequest): Future[(Session, String)] = {
    import spray.json._
    val wxUrl = s"https://api.weixin.qq.com/sns/jscode2session?appid=${appId}&secret=${appSecret}&js_code=${request.code}&grant_type=authorization_code"

    for {
      resposne <- Http().singleRequest(HttpRequest(uri = wxUrl, method = HttpMethods.GET))
      entity <- resposne.entity.toStrict(10.seconds)
      wxSession <- entity.dataBytes.runFold(ByteString.empty) { case (acc, b) => acc ++ b } map {
        _.decodeString("UTF-8").parseJson.convertTo[WxSession]
      }

      // todo:
      // check request with wxSession.session_key
      // compare openid if equal, upsert request里的用户信息到mongodb

      // 保存session到redis
      session = Session(wxSession.session_key)
      ok: Boolean <- redis.setex(wxSession.openid, wxSession.expires_in, session)

      result = if (ok) {
        (session, wxSession.openid)
      } else {
        throw DatabaseException("save session failed")
      }
    } yield result
  }

  val openid: Directive1[String] = headerValueByName("X-OPENID")

  def optSession(openid: String): Directive1[Future[Option[Session]]] = provide(redis.get(openid))

  // API接口定义
  case class Pagination(totalSize: Int, totalPage: Int, pageSize: Int, curPage: Int)

  case class Error(code: Int, message: String)

  case class Result[T](data: Option[T], success: Boolean, error: Option[Error] = None, pagination: Option[Pagination] = None)

  implicit val PaginationFormat = jsonFormat4(Pagination)
  implicit val ErrorFormat = jsonFormat2(Error)

  implicit def resultFormat[A: JsonFormat] = jsonFormat3(Result.apply[A])

  def failed(code: Int, message: String) = Result[Int](None, false, Some(Error(code, message)))

  def success[T](data: T, pageInfo: Option[Pagination] = None): Result[T] = Result(Some(data), success = true, pagination = pageInfo)

  // 异常定义
  case class DatabaseException(message: String) extends Exception

  case class ParameterException(message: String) extends Exception

  case class BusinessException(message: String) extends Exception

  case class UnauthorizedException(message: String) extends Exception

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

    case e: UnauthorizedException =>
      extractUri { uri =>
        println(s"Request to $uri could not be handled normally!!!!!!!!! UnauthorizedException11111111 {}", e.message)
        complete(HttpResponse(StatusCodes.InternalServerError, entity = failed(401, "X-OPENID required").toJson.toString))
      }

    case NonFatal(e) =>
      extractUri { uri =>
        log.error(s"Request to $uri could not be handled normally!!!!!!!!!")
        log.error("{}", e.getStackTrace.mkString("\n"))
        complete(HttpResponse(StatusCodes.InternalServerError, entity = Result(data = Some("error"), error = Some(Error(500, "系统错误"))).toJson.toString))
      }
  }

  // akka-http rejection handler
  implicit def myRejectionHandler =
    RejectionHandler.newBuilder().handle {
      case MissingCookieRejection(cookieName) =>
        extractUri { uri =>
          log.warning(s"$uri No cookies, no service!!!")
          complete(HttpResponse(BadRequest, entity = failed(BadRequest.intValue, "No cookies, no service!!!").toJson.toString()))
        }

      case MissingQueryParamRejection(name) =>
        extractUri { uri =>
          log.warning(s"$uri miss request parameter $name")
          complete((Forbidden, s"miss request parameter $name"))
        }

      case AuthorizationFailedRejection =>
        extractUri { uri =>
          log.warning(s"$uri You're out of your depth!")
          complete((Forbidden, "You're out of your depth!"))
        }

      case ValidationRejection(msg, _) =>
        extractUri { uri =>
          log.warning(s"$uri That wasn't valid! {}", msg)
          complete((InternalServerError, "That wasn't valid! " + msg))
        }
      case MissingHeaderRejection(header) =>
        extractUri { uri =>
          if (header == "X-OPENID") {
            log.warning("Unauthorized access to {} ", uri)
            throw UnauthorizedException("kkk")
          } else {
            throw UnauthorizedException("kkk")
          }
        }
    }.handleAll[MethodRejection] { methodRejections =>
      extractUri { uri =>
        val names = methodRejections.map(_.supported.name)
        log.warning("{} Can't do that! Supported: {} ", uri, names)
        complete((MethodNotAllowed, s"Can't do that! Supported: ${names mkString " or "}!"))
      }
    }.handleNotFound {
      complete(HttpResponse(StatusCodes.NotFound, entity = "Not found"))
    }.result()


  //mongo
  implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
    override val converter: (Document) => String = (doc) => doc.toJson
  }

  implicit class GenericObservable[C](val observable: Observable[C]) extends ImplicitObservable[C] {
    override val converter: (C) => String = (doc) => doc.toString
  }

  trait ImplicitObservable[C] {
    val observable: Observable[C]
    val converter: (C) => String

    def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))

    def headResult(): C = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))

    def printResults(initial: String = ""): Unit = {
      if (initial.length > 0) print(initial)
      results().foreach(res => println(converter(res)))
    }

    def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")
  }

}


