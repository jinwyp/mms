package com.gongshijia.mms.mongo

import org.mongodb.scala.bson.ObjectId

/**
  * Created by xiangyang on 2017/5/5.
  */
object MongoModels {


  object OSSAsset {
    def apply(url: String, assetType: String): OSSAsset = OSSAsset(new ObjectId(), url, assetType);
  }

  case class OSSAsset(_id: ObjectId, url: String, assetType: String)

}
