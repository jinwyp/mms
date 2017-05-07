package com.gongshijia.mms.mongo

import java.util.Date

import org.mongodb.scala.bson.ObjectId

/**
  * Created by xiangyang on 2017/5/5.
  */
object MongoModels {


  case class OSSAsset(_id: ObjectId, url: String, assetType: String)

  object OSSAsset {
    def apply(url: String, assetType: String): OSSAsset = OSSAsset(new ObjectId(), url, assetType);
  }


  case class User(_id: ObjectId, union_id: Option[String], name: Option[String], nickname: String, gender: Int, city: String, province: String, avatarUrl: String, arts: List[String],
                  workBeg: Option[Date], workEnd: Option[Date], workAddress: Option[String], phone: Option[String],
                  shopName: Option[String], lastUpdate: Date)

  object User {
    def apply(union_id: Option[String], name: Option[String], nickname: String, gender: Int, city: String, province: String, avatarUrl: String, arts: List[String],
              workBeg: Option[Date], workEnd: Option[Date], workAddress: Option[String], phone: Option[String],
              shopName: Option[String], lastUpdate: Date): User = User(new ObjectId(), union_id, name, nickname, gender, city, province, avatarUrl, arts,
      workBeg, workEnd, workAddress, phone,
      shopName, lastUpdate);
  }

  case class AccountRes( status: Int, errMess:String)
}
