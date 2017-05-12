package com.gongshijia.mms.mongo

import java.util.Date

import org.mongodb.scala.bson.ObjectId

/**
  * Created by xiangyang on 2017/5/5.
  */
trait MongoModels {


  case class OSSAsset(_id: ObjectId, url: String, assetType: String)

  object OSSAsset {
    def apply(url: String, assetType: String): OSSAsset = OSSAsset(new ObjectId(), url, assetType);
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

}

object MongoModels extends MongoModels
