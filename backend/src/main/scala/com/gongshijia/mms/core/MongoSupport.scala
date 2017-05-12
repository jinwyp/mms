package com.gongshijia.mms.core

import java.util.Date

import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY

/**
  * Created by hary on 2017/5/12.
  */
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
