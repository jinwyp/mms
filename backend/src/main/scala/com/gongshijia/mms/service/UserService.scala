package com.gongshijia.mms.service

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest}
import akka.util.ByteString
import com.gongshijia.mms.login.Models
import com.gongshijia.mms.mongo.MongoModels
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by hary on 2017/5/3.
  */
trait UserService extends Models with MongoModels {


  private val codecRegistry = fromRegistries(fromProviders(classOf[User]), DEFAULT_CODEC_REGISTRY);


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

      // todo:
      // check request with wxSession.session_key
      // compare openid if equal, upsert request里的用户信息到mongodb
      collection: MongoCollection[User] = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry).getCollection("userinfo")

      user = User(null, wxSession.openid, login.avatarUrl, login.country, login.province, login.city, login.gender, login.language, login.nickName)
      a <- collection.insertOne(user).toFuture().map(_ => true)
     b = if (a) {
       1
      }else{
       2
    }
      //      updateResult: UpdateResult <- collection.updateOne(equal("openid", wxSession.openid), BsonDocument(user.toJson.toString())).toFuture()

      //      bb = if (updateResult.isModifiedCountAvailable) {
      //        1
      //      } else {
      //        2
      //      }

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


  //  def upsertUser(user: User): Future[UpdateResult] = {
  //    val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
  //    val collection: MongoCollection[User] = database.getCollection("userinfo")
  //    println(user.toJson.toString);
  //    val result: Future[UpdateResult] = collection.updateOne(equal("openid", user.code), BsonDocument(user.toJson.toString())).toFuture()
  //    if (result.isCompleted) {
  //
  //    }
  //    result;

  //  }

}
