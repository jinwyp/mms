package com.gongshijia.mms.user.login

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import com.gongshijia.mms._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.Future
import scala.concurrent.duration._


/**
  * Created by hary on 2017/5/12.
  */
trait LoginService extends ApiSupport with Core with HttpSupport with AppConfig with MongoSupport with RedisSupport {

  import LoginModels._

  // 获取WxSession
  def createSession(login: LoginRequest): Future[(Session, String)] = {

    case class WxSession(openid: String, session_key: String, expires_in: Long)
    implicit val WxSessionFormat = jsonFormat3(WxSession)

    import spray.json._
    val wxUrl = s"https://api.weixin.qq.com/sns/jscode2session?appid=${appId}&secret=${appSecret}&js_code=${login.code}&grant_type=authorization_code"
    for {
      response <- Http().singleRequest(HttpRequest(uri = wxUrl, method = HttpMethods.GET))
      entity <- response.entity.toStrict(10.seconds)
      wxSession <- entity.dataBytes.runFold(ByteString.empty) {
        case (acc, b) => acc ++ b
      } map {
        _.decodeString("UTF-8").parseJson.convertTo[WxSession]
      };
      // todo :check request with wxSession.session_key
      user = User(wxSession.openid, login.avatarUrl, login.country, login.province, login.city, login.gender, login.language, login.nickName)
      upsertResult <- upsertUser(user)
      // 保存session到redis
      session = Session(wxSession.session_key)
      ok: Boolean <- redisClient.setex(wxSession.openid, wxSession.expires_in, session)
      result = if (ok) {
        (session, wxSession.openid)
      } else {
        throw DatabaseException("save session failed")
      }
    } yield result
  }


  def upsertUser(user: User): Future[UpdateResult] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    val updateOptions = UpdateOptions().upsert(true)
    val updateBson = combine(set("openid", user.openid), set("avatarUrl", user.avatarUrl), set("country", user.country),
      set("province", user.province), set("city", user.city), set("gender", user.gender), set("language", user.language),
      set("nickName", user.nickName))
    collection.updateOne(equal("openid", user.openid), updateBson, updateOptions).toFuture()
  }


}
