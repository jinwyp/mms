package com.gongshijia.mms.service

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import com.gongshijia.mms.Core
import com.gongshijia.mms.login.Models
import com.gongshijia.mms.mongo.MongoModels
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.result.UpdateResult
import org.mongodb.scala.{MongoCollection, MongoDatabase}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by hary on 2017/5/3.
  */
trait UserService extends Core with Models with MongoModels {


  private val codecRegistry = CodecRegistries.fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY);

  // 获取WxSession
  def createSession(login: LoginRequest): Future[(Session, String)] = {
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
      ok: Boolean <- redis.setex(wxSession.openid, wxSession.expires_in, session)
      result = if (ok) {
        (session, wxSession.openid)
      } else {
        throw DatabaseException("save session failed")
      }
    } yield result
  }

  def upsertUser(user: User): Future[UpdateResult] = {
    val database: MongoDatabase = mongoDatabase.withCodecRegistry(codecRegistry)
    val collection: MongoCollection[User] = database.getCollection("userinfo")
    val updateOptions = UpdateOptions().upsert(true)
    val updateBson = combine(set("openid", user.openid), set("avatarUrl", user.avatarUrl), set("country", user.country),
      set("province", user.province), set("city", user.city), set("gender", user.gender), set("language", user.language),
      set("nickName", user.nickName))
    collection.updateOne(equal("openid", user.openid), updateBson, updateOptions).toFuture()
  }

  //添加关注的类目
  def addArtsList(openId: String, artsList: List[String]): Future[UpdateResult] = {
    val database: MongoDatabase = mongoDatabase.withCodecRegistry(codecRegistry)
    val collection: MongoCollection[User] = database.getCollection("userinfo")
    collection.updateOne(equal("openid", openid), pushEach("arts", artsList)).toFuture()
  }

  def findArtsList(openId: String): Future[Option[List[String]]] ={
    val database: MongoDatabase = mongoDatabase.withCodecRegistry(codecRegistry)
    val collection: MongoCollection[User] = database.getCollection("userinfo")
    collection.find(equal("openid",openId)).first().toFuture().map(_.categories)
  }

}
