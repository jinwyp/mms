package com.gongshijia.mms.service

import com.gongshijia.mms.Core
import com.gongshijia.mms.mongo.MongoModels.OSSAsset
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
import org.mongodb.scala.{Completed, MongoCollection, MongoDatabase, Observable}

import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/5.
  */
trait AssetService extends Core {

  def insertUploadRecord(filename: String, mimeType: String): Future[Completed] = {
    val codecRegistry = fromRegistries(fromProviders(classOf[OSSAsset]), DEFAULT_CODEC_REGISTRY)
    val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry);
    val collection: MongoCollection[OSSAsset] = database.getCollection("uploadFile_record");
    collection.insertOne(OSSAsset(filename, mimeType)).toFuture();
  }


}