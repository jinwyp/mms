package com.gongshijia.mms.user.login

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import com.gongshijia.mms.core._
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._

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
      user = User(new ObjectId(), wxSession.openid, login.avatarUrl, login.country, login.province, login.city, login.gender, login.language, login.nickName)
      upsertResult <- loginUpsertUser(user)
      // 保存session到redis
      session = Session(wxSession.session_key)
      ok: Boolean <- redisClient.setex(wxSession.openid, wxSession.expires_in, session)
      result = if (ok && upsertResult) {
        (session, wxSession.openid)
      } else {
        throw DatabaseException("save session failed")
      }
    } yield result
  }


  def loginUpsertUser(user: User): Future[Boolean] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", user.openid))
      .first()
      .toFuture()
      .map { u =>
        if (u == null) {
          collection.insertOne(user).toFuture()
          true
        } else {
          val updateOptions = UpdateOptions().upsert(true)
          val updateBson = combine(set("openid", user.openid), set("avatarUrl", user.avatarUrl), set("country", user.country),
            set("province", user.province), set("city", user.city), set("gender", user.gender), set("language", user.language),
            set("nickName", user.nickName))
          collection.updateOne(equal("openid", user.openid), updateBson, updateOptions).toFuture().map(_ => true)
          true
        }
      }

  }

  def findUserByOpenId(openid: String): Future[User] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", openid)).first().toFuture().map(t=>
      t.copy(wxQrCode = Some(localossHost.concat(t.wxQrCode.getOrElse(""))))
   )
  }

  //手艺人完善个人信息
  def updateUserInfo(openid: String, ur: UserUpdateRequest): Future[Boolean] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    val updateBson = combine(set("phone", ur.phone), set("shopName", ur.shopName),
      set("workAddress", ur.workAddress), set("wxNum", ur.wxNum), set("wxQrCode", ur.wxQrCode),
      set("workBeg", ur.workBeg), set("workEnd", ur.workEnd), set("userName", ur.userName),
      set("workLon", ur.workLon), set("workLat", ur.workLat), set("workDay", ur.workDay))
    val updateOptions = UpdateOptions().upsert(true)
    collection.updateOne(equal("openid", openid), updateBson, updateOptions).toFuture().map(_ => true)
  }

}
