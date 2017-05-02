package com.gongshijia.mms.asset

import java.io.File
import java.nio.file.Paths
import java.nio.file.StandardOpenOption.{CREATE, WRITE}
import java.util.UUID

import akka.http.scaladsl.model.ContentTypes.`application/octet-stream`
import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.server.Directives.{complete, extractRequestContext, fileUpload, get, onSuccess, parameter, path, post}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo
import akka.stream.IOResult
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString
import com.gongshijia.mms.Core
import org.apache.commons.io.FileUtils
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.model.ContentTypes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{ContentDispositionTypes, `Content-Disposition`}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.FileInfo

import scala.concurrent.Future

/**
  * Created by hary on 2017/5/2.
  */
trait AssetRoute extends Core with SprayJsonSupport {

  import Models._


  implicit val assetRouteExecutionContext = coreSystem.dispatcher

  val accessId = coreSystem.settings.config.getString("aliyun.accessId")
  val accessKey = coreSystem.settings.config.getString("aliyun.accessKey")

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


  // GET /asset/policy
  def assetUploadPolicy = get {
    path("policy") {
      complete(PolicyResponse("callback", "signature", "policy", accessId))
      // complete("ok")
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

  def assetRoute = assetUploadPolicy ~ assetUploadFile

}


