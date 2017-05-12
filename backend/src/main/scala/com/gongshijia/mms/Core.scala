package com.gongshijia.mms

import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.StatusCodes.BadRequest
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.LogEntry
import akka.stream.ActorMaterializer
import akka.util.ByteString
import org.anormcypher.{Neo4jConnection, Neo4jREST}
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{Document, MongoClient, Observable}
import play.api.libs.ws.ning
import redis.{ByteStringFormatter, RedisClient}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
import scala.util.control.NonFatal

/**
  * Created by hary on 2017/5/12.
  */
trait Core {
  protected def mkSystem: ActorSystem = ActorSystem("jfc-system")

  // 系统， 配置， 日志, 线程池
  implicit val coreSystem: ActorSystem = mkSystem
  implicit val coreMaterializer = ActorMaterializer()
  implicit val coreExecutionContext = coreSystem.dispatcher
  val coreConfig = coreSystem.settings.config
  val log = Logging(coreSystem, this.getClass)

  //
  case class Session(session_key: String)

}

trait AppConfig extends Core {
  val accessKeyId = coreConfig.getString("aliyun.accessKeyId")
  val accessKeySecret = coreConfig.getString("aliyun.accessKeySecret")
  val ossBucket = coreConfig.getString("aliyun.ossBucket")
  val ossHost = coreConfig.getString("aliyun.ossHost")
  val jfcDomain = coreConfig.getString("jfc.domain")

  //
  val appId = coreConfig.getString("wx.appId")
  val appSecret = coreConfig.getString("wx.appSecret")
}


trait MongoTestSupport {

  // mongo
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

object MongoTestSupport extends MongoTestSupport

// mongodb support
trait MongoSupport extends Core {

  // 模型注册!

  // 阿里云oss文件模型
  case class OSSAsset(_id: ObjectId, url: String, mimeType: String)

  object OSSAsset {
    def apply(url: String, mimeType: String): OSSAsset = OSSAsset(new ObjectId(), url, mimeType);
  }

  //
  case class User(_id: ObjectId, openid: String, avatarUrl: String, country: String, province: String, city: String,
                  gender: Int, language: String, nickName: String,
                  workBeg: Option[Date] = None, workEnd: Option[Date] = None, workAddress: Option[String] = None,
                  phone: Option[String] = None, shopName: Option[String] = None, lastUpdate: Option[Date] = None,
                  categories: Option[List[String]] = None);

  object User {
    def apply(openid: String, avatarUrl: String, country: String, province: String, city: String,
              gender: Int, language: String, nickName: String): User = User(new ObjectId(), openid, avatarUrl, country, province, city, gender, language, nickName);
  }

  val codecRegistry = fromRegistries(fromProviders(
    classOf[OSSAsset], // 添加更多的models在这里
    classOf[User] // 添加更多的models在这里
  ), DEFAULT_CODEC_REGISTRY)

  val mongoClient = MongoClient(coreConfig.getString("mongo.uri"))
  val mongoDb = mongoClient.getDatabase(coreConfig.getString("mongo.database")).withCodecRegistry(codecRegistry)
  def getCollection(name: String) = mongoDb.getCollection(name)

}

// redis support
trait RedisSupport extends Core {

  val redisClient = RedisClient(coreConfig.getString("redis.host"), coreConfig.getInt("redis.port"))

}

trait ApiSupport extends DefaultJsonProtocol {

  import spray.json.JsonFormat

  // API接口定义
  case class Pagination(totalItem: Int, totalPage: Int, pageSize: Int, curPage: Int)

  case class Error(code: Int, message: String)

  case class Result[T](data: Option[T], success: Boolean, error: Option[Error] = None)

  implicit val PaginationFormat = jsonFormat4(Pagination)
  implicit val ErrorFormat = jsonFormat2(Error)

  implicit def resultFormat[A: JsonFormat] = jsonFormat3(Result.apply[A])

  def failed(code: Int, message: String) = Result[Int](None, false, Some(Error(code, message)))

  def success[T](data: T): Result[T] = Result(Some(data), success = true)

  implicit class DomainFutureToResult[T](df: Future[T]) {
    def toResult(implicit ec: ExecutionContext): Future[Result[T]] = df.map(success _)
  }

}

// 异常定义
trait ExceptionSupport {

  sealed trait InternalException extends Exception {
    val message: String
  }

  case class DatabaseException(message: String) extends InternalException

  case class ParameterException(message: String) extends InternalException

  case class BusinessException(message: String) extends InternalException

  case class UnauthorizedException(message: String) extends InternalException

}

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
    }.handleAll[MethodRejection] { methodRejections =>
      extractUri { uri =>
        val names = methodRejections.map(_.supported.name).mkString(",")
        completeReject(uri, BadRequest, s"only support method: $names")
      }
    }.handleNotFound {
      extractUri { uri => completeReject(uri, BadRequest, s"not found") }
    }.result()
}

trait Neo4jSupport extends Core {
  // Provide an instance of WSClient
  val wsclient = ning.NingWSClient()
  implicit val connection: Neo4jConnection = Neo4jREST(
    coreConfig.getString("neo4j.host"),
    coreConfig.getInt("neo4j.port"),
    coreConfig.getString("neo4j.username"),
    coreConfig.getString("neo4j.password"))(wsclient)
}
