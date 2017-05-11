package com.gongshijia.mms.mongo

import com.gongshijia.mms.Core
import org.mongodb.scala.bson.ObjectId

/**
  * Created by xiangyang on 2017/5/5.
  */
trait MongoModels extends Core {


  case class OSSAsset(_id: ObjectId, url: String, assetType: String)

  object OSSAsset {
    def apply(url: String, assetType: String): OSSAsset = OSSAsset(new ObjectId(), url, assetType);
  }


  case class User(_id: ObjectId, openid: String, avatarUrl: String, country: String, province: String, city: String, gender: Int, language: String, nickName: String);

  implicit val UserFormat = jsonFormat9(User);


}

object MongoModels extends MongoModels
