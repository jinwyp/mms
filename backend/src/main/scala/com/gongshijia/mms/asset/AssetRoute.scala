package com.gongshijia.mms.asset

import java.io.File
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, WRITE}
import java.text.SimpleDateFormat
import java.util.{Locale, TimeZone}
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

  val accessKeyId = coreConfig.getString("aliyun.accessKeyId")
  val accessKeySecret = coreConfig.getString("aliyun.accessKeySecret")
  val ossBucket = coreConfig.getString("aliyun.ossBucket")
  val ossHost = coreConfig.getString("aliyun.ossHost")
  val domain = coreConfig.getString("mms.domain")

  val fileRoot = "/tmp" // coreConfig.getString("file.root")

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
        PolicyResponse(callback, getSignature(policy.getBytes(), accessKeySecret.getBytes()), policy, accessKeyId, ossHost)
      })
    }
  }


  // 阿里云上传成功回调
  def assetOssCallback: Route = path("callback") {
    formFields("filename", "mimeType") { (filename, mimeType) =>
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


  // 阿里云获取下载资源签名
  def assetOssDownload: Route = path("assetDownload") {
    formFields("filename") { (filename) =>
//      val format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
//      format.setTimeZone(TimeZone.getTimeZone("GMT"));
      //      val dateStr = format.format(new Date());
      val sb = "GET"+"\n"+"Wed, 03 May 2017 20:12:41 GMT"+"\n"+filename;
      println(sb);
      val signature = getSignature(sb.getBytes(), accessKeySecret.getBytes());
      complete("OSS " + accessKeyId + ":" + signature)
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

  def assetRoute = assetUploadPolicy ~ assetOssCallback ~ assetOssDownload ~ path("hello") {
    complete("hello")
  }

}


