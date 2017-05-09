package com.gongshijia.mms.asset

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.Core
import com.gongshijia.mms.service.AssetService



/**
  * Created by hary on 2017/5/2.
  */
trait AssetRoute extends Core with SprayJsonSupport with Models with AssetService{

  // 获取上传文件签名
  private def getSignature(data: Array[Byte], key: Array[Byte]): String = {
    val signingKey: SecretKeySpec = new SecretKeySpec(key, HMAC_SHA1);
    val mac = Mac.getInstance("HmacSHA1");
    mac.init(signingKey);
    val rawHmac = mac.doFinal(data);
    base64Encode(rawHmac);
  }

  val callbackBody = "filename=${object}&size=${size}&mimeType=${mimeType}&height=${imageInfo.height}&width=${imageInfo.width}";
  val callback = base64Encode(
    s"""
       |{
       |"callbackUrl":"http://$domain/asset/callback",
       |"callbackBody":"$callbackBody",
       |"callbackBodyType":"application/x-www-form-urlencoded"
       |}
    """.stripMargin.getBytes()
  )

  private def getPolicy: String = {
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

  private def base64Encode(origin: Array[Byte]): String = {
    if (null == origin) {
      return null;
    }
    new sun.misc.BASE64Encoder().encode(origin).replace("\n", "").replace("\r", "");
  }


  // GET /asset/policy
  def assetUploadPolicy = get {
    path("policy") {
      (get & openid) { id =>
        extractExecutionContext { implicit ec =>
          complete({
            val policy = base64Encode(getPolicy.getBytes());
            PolicyResponse(callback, getSignature(policy.getBytes(), accessKeySecret.getBytes()), policy, accessKeyId, ossHost, id);
          })
        }
      }
    }
  }


  // 阿里云上传成功回调
  def assetOssCallback: Route = path("callback") {
    formFields("filename", "mimeType") { (filename, mimeType) =>
      onSuccess(insertUploadRecord(filename, mimeType)) {
        case true =>
          val resultStr =
            s"""
               |{
               |"filename": "$filename",
               |"mimeType": "$mimeType"
               |}
                   """.stripMargin
          complete(resultStr);

      }
    }
  }

  def assetRoute = assetUploadPolicy ~ assetOssCallback

}


