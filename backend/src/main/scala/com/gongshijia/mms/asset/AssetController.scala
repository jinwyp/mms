package com.gongshijia.mms.asset

import akka.http.scaladsl.server.Directives.{complete, onSuccess}
import akka.http.scaladsl.server.StandardRoute
import com.gongshijia.mms.core.Utils

import scala.concurrent.Future

/**
  * Created by hary on 2017/5/12.
  */
trait AssetController extends AssetService {

  import AssetModels._

  def handleUploadPolicy(openid: String): PolicyResponse = {
    val policy = Utils.base64Encode(getPolicy.getBytes());
    PolicyResponse(
      AssetServiceUtils.callback,
      Utils.hmacSHA1(accessKeySecret.getBytes(), policy.getBytes()),
      policy,
      accessKeyId,
      ossHost,
      openid)
  }

  def handleOssCallback(filename: String, mimeType: String): Future[String] = {
    insertUploadRecord(filename, mimeType).map {
      case true =>
          s"""
             |{
             |"success": true,
             |"filename": "$filename",
             |"mimeType": "$mimeType"
             |}
                   """.stripMargin
      case false =>
          s"""
             |{
             |"success": false,
             |"filename": "$filename",
             |"mimeType": "$mimeType"
             |}
                   """.stripMargin

    }
  }

}
