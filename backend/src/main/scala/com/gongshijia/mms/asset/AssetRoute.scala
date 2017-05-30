package com.gongshijia.mms.asset

import java.io.{File, FileOutputStream}
import java.util.UUID

import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import akka.stream.IOResult
import akka.stream.scaladsl.{Keep, Sink, Source, StreamConverters}
import akka.util.ByteString
import com.gongshijia.mms.asset.AssetModels.OSSResponse
import com.gongshijia.mms.core.{Core, HttpSupport}
import org.apache.commons.io.FileUtils

import scala.concurrent.Future
import scala.tools.nsc.interpreter.OutputStream

/**
  * Created by hary on 2017/5/2.
  */
trait AssetRoute extends Core with HttpSupport with AssetController {

  // GET /asset/policy
  def assetUploadPolicy = (path("policy") & get & openid) { id =>
    complete(success(handleUploadPolicy(id)))
  }

  // 阿里云上传成功回调 ---应答给---> 阿里云 ---> 浏览器
  def assetOssCallback = (path("callback") & formFields("filename", "mimeType")) { (filename, mimeType) =>
    complete(OSSResponse(filename, mimeType))
  }

  def mockOss = path("oss") {

    extractRequest { req =>
      req.headers.foreach(h => log.info("got header: {} -> {}", h.name(), h.value()))
      formFieldMap { (vars: Map[String, String]) =>
        val file = vars("file")
        log.info("oss got: {}", vars - "file")
        complete("ok")
      }
    }
  }


  //asset/upload
  private def assetUpload = (post & withSizeLimit(100 * 1024 * 1024)) {
    (path("upload") & extractRequestContext) { ctx =>
      implicit val materializer = ctx.materializer
      fileUpload("file") {
        case (metadata, src: Source[ByteString, Any]) =>
          val suffix = metadata.fileName.substring(metadata.fileName.indexOf("."))
          val fileName =  UUID.randomUUID().toString.concat(suffix)
          val file = new File(fileRoot + fileName)
          FileUtils.forceMkdir(file.getParentFile)
          val outpuStream: OutputStream = new FileOutputStream(file)

          val sink: Sink[ByteString, Future[IOResult]] =
            StreamConverters.fromOutputStream(() => outpuStream)
          val mat: Future[IOResult] = src.toMat(sink)(Keep.right).run()

          val f = for {
            _ <- mat
          } yield  fileName
          complete(f.toResult)
      }
    }
  }

  def staticResourceRoute = (get & path("static" / Segment)) { fileId =>
    val file = new File(fileRoot + fileId)
    getFromFile(file)
  }

  def assetRoute = assetUploadPolicy ~ assetOssCallback ~ assetUpload ~ staticResourceRoute

}


