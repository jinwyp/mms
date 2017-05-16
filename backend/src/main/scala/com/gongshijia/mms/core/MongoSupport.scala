package com.gongshijia.mms.core

import java.util.Date

import org.bson.BsonDocument
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.MongoClient
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.bson.conversions.Bson

/**
  * Created by hary on 2017/5/12.
  */
// mongodb support
trait MongoSupport extends Core {

  // 模型注册!

  // 阿里云oss文件模型
  case class OSSAsset(_id: ObjectId, url: String, mimeType: String, aaa: Option[String] = None)

  object OSSAsset {
    def apply(url: String, mimeType: String): OSSAsset = OSSAsset(new ObjectId(), url, mimeType);
  }

  case class User(_id: ObjectId, openid: String, avatarUrl: String, country: String, province: String, city: String,
                  gender: Int, language: String, nickName: String,
                  workBeg: Option[Date] = None, workEnd: Option[Date] = None, workAddress: Option[String] = None,
                  phone: Option[String] = None, shopName: Option[String] = None, lastUpdate: Option[Date] = None,
                  categories: Option[List[String]] = None);

  object User {
    def apply(openid: String, avatarUrl: String, country: String, province: String, city: String,
              gender: Int, language: String, nickName: String): User = User(new ObjectId(), openid, avatarUrl, country, province, city, gender, language, nickName);
  }

  // 耗材
  case class Material(name: String, count: Int)

  // 流程
  case class ArtFlow(flow: String, duration: Int)

  //署名信息
  case class SignInfo(_id: ObjectId, openid: String, checked: Int = 0, realPicture: List[String], material: List[Material], flows: List[ArtFlow])


  //评论
  case class Comments(_id: ObjectId, openid: String, createDate: Date, content: String)

  object Comments {
    def apply(openid: String, createDate: Date, content: String): Comments =
      new Comments(new ObjectId(), openid, createDate, content)
  }

  // 体验报告
  case class ExperienceReport(_id: ObjectId,
                              lon: Double,
                              lat: Double,
                              locationName: String,
                              shopName: String,
                              expTime: Date,
                              expPrice: Double,
                              pictures: List[String],
                              videos: Option[String],
                              feeling: String,
                              category: String,
                              privacy: Int,
                              pricePrivacy: Int,
                              openid: String,
                              signInfo: Option[List[SignInfo]],
                              comments: Option[List[Comments]]);

  object ExperienceReport {
    def apply(lon: Double,
              lat: Double,
              locationName: String,
              shopName: String,
              expTime: Date,
              expPrice: Double,
              pictures: List[String],
              videos: Option[String],
              feeling: String,
              category: String,
              privacy: Int,
              pricePrivacy: Int,
              openid: String): ExperienceReport = new ExperienceReport(new ObjectId(), lon, lat, locationName, shopName, expTime, expPrice, pictures, videos, feeling, category, privacy, pricePrivacy, openid, None, None)
  }

  val codecRegistry = fromRegistries(fromProviders(
    classOf[OSSAsset], // 添加更多的models在这里
    classOf[User],
    classOf[ExperienceReport],
    classOf[SignInfo],
    classOf[Comments],
    classOf[Material],
    classOf[ArtFlow]
  ), DEFAULT_CODEC_REGISTRY)

  val mongoClient = MongoClient(coreConfig.getString("mongo.uri"))
  val mongoDb = mongoClient.getDatabase(coreConfig.getString("mongo.database")).withCodecRegistry(codecRegistry)

  def toBson(bson: Bson): Document = Document(bson.toBsonDocument(classOf[BsonDocument], MongoClient.DEFAULT_CODEC_REGISTRY))

  def getCollection(name: String) = mongoDb.getCollection(name)

}
