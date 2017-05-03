package com.gongshijia.mms.asset

import java.io.File
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, WRITE}
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, extractRequestContext, fileUpload, get, onSuccess, path, post, _}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import com.gongshijia.mms.Core
import org.apache.commons.io.FileUtils

import scala.concurrent.Future;


/**
  * Created by hary on 2017/5/2.
  */
trait AssetRoute extends Core with SprayJsonSupport {

  import Models._


  implicit val assetRouteExecutionContext = coreSystem.dispatcher

  val accessId = coreSystem.settings.config.getString("aliyun.accessKeyId")
  val accessKey = coreSystem.settings.config.getString("aliyun.accessKeySecret")
  val ossBucket = coreSystem.settings.config.getString("aliyun.ossBucket")
  val ossHost = coreSystem.settings.config.getString("aliyun.ossHost")
  val domain = coreSystem.settings.config.getString("mms.domain")

  val fileRoot = "/tmp" // coreSystem.settings.config.getString("file.root")

  //  private def getAssetInfo(id: String): Future[(String, String, Source[ByteString, Future[IOResult]])] = {
  //    val query = assetClass.filter(f => f.asset_id === id).map { e =>
  //      (e.url, e.file_type, e.origin_name)
  //    }.result.head
  //    dbrun(query).recover {
  //      case _ =>
  //        log.error("Database error in downloadFile")
  //        throw new DatabaseException("该文件不存在")
  //    } map {
  //      case (url, fileType, name) =>
  //        (fileType, name, FileIO.fromPath(Paths.get(fileRoot + File.separator + url)))
  //    }
  //  }

  /**
    * GET asset/:asset_id  -- 下载asset_id资源
    */

  //  private def downloadFile: Route = get {
  //    path("download" / Segment) { id =>
  //      onSuccess(getAssetInfo(id)) {
  //        case (_, name, source) =>
  //          complete(
  //            HttpResponse(
  //              status = StatusCodes.OK,
  //              headers = List(`Content-Disposition`(ContentDispositionTypes.attachment, Map("filename" -> name))),
  //              entity = HttpEntity(`application/octet-stream`, source)
  //            )
  //          )
  //      }
  //    }
  //  }


  // 获取签名
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
      complete({
        val policy = base64Encode(getPolicy.getBytes());
        PolicyResponse(callback, getSignature(policy.getBytes(), accessKey.getBytes()), policy, accessId, ossHost)
      })
    }
  }


  // 阿里云上传回调
  def assetOssCallback: Route = path("callback") {
    formFields("filename", "mimeType", "width", "height") { (filename, mimeType, width, height) =>
      val resultStr =
        s"""
           |{
           |"filename": "$filename"
           |"mimeType": "$mimeType"
           |"width":  "$width"
           |"height": "$height"
           |}
                   """.stripMargin
      complete(resultStr);
    }
  }


  val fileField = "key"

  // POST /asset/upload
  def assetUploadFile: Route = path("upload") {
    post {
      extractRequestContext { ctx =>
        implicit val materializer = ctx.materializer

        fileUpload(fileField) {
          case (meta: FileInfo, source: Source[ByteString, Any]) =>

            println("asset id is " + meta.fileName)
            val gdir = fileRoot + File.separator
            FileUtils.forceMkdir(new File(gdir))

            val sink = FileIO.toPath(Paths.get(gdir + File.separator + meta.fileName), Set(CREATE, WRITE))

            val ff: Future[IOResult] = for {
              result <- source.toMat(sink)(Keep.right).run
            } yield result

            onSuccess(ff) {
              case result =>
                log.info(s"upload file[${meta.fileName}] size[${result.count}] status[${result.status}]")
                complete("success")
            }
        }
      }
    }
  }

  def assetRoute = assetUploadPolicy ~ assetUploadFile ~ assetOssCallback

}


