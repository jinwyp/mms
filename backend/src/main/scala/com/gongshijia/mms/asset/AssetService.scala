package com.gongshijia.mms.asset

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1

import com.gongshijia.mms.core.{AppConfig, MongoSupport, Utils}
import org.mongodb.scala.MongoCollection

import scala.concurrent.Future

object AssetServiceUtils extends AppConfig {

  val callbackBody = "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}";
  val callback = Utils.base64Encode(
    s"""
       |{
       |"callbackUrl":"http://$mmsDomain/asset/callback",
       |"callbackBody":"$callbackBody",
       |"callbackBodyType":"application/x-www-form-urlencoded"
       |}
    """.stripMargin.getBytes()
  )

}

/**
  * Created by xiangyang on 2017/5/5.
  */
trait AssetService extends MongoSupport with AppConfig {

  def insertUploadRecord(filename: String, mimeType: String): Future[Boolean] = {
    val collection: MongoCollection[OSSAsset] = mongoDb.getCollection("uploadFile_record");
    collection.insertOne(OSSAsset(filename, mimeType)).toFuture().map { _ => true }
  }

  def getPolicy: String = {
    val expire = "2120-01-01T12:00:00.000Z" // todo: should be now + 10 minutes
    s"""
       |{
       |"expiration": "$expire",
       |"conditions": [
       |  ["content-length-range", 0, 1073741824]
       |]
       |}
    """.stripMargin
  }
}
