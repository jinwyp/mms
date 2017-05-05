package com.gongshijia.mms.misc


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

import scala.concurrent.{ExecutionContext, Future}
import javax.crypto.spec.SecretKeySpec
import java.io.IOException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException

import org.apache.commons.codec.binary.Base64
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1

import akka.http.scaladsl.Http
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.gongshijia.mms.Core
import com.gongshijia.mms.service.UserService

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by hary on 2017/5/4.
  */
trait MiscRoute extends SprayJsonSupport with Core with Models{

  def handleGetCategorys(openid: String)(implicit ec: ExecutionContext) = {
    // todo:  依据openid 从mongo中拿出这个人的兴趣
    val fis: Future[List[String]] = Future.successful(List("造型")) // 获取兴趣
    fis map { is =>
      val arts: Seq[CategoryResult] = artsList.map { art =>
        if (is.contains(art.name)) {
          art.copy(checked = true)
        } else {
          art
        }
      }
      success(arts)
    }
  }

  def getCategorys = path("arts") {
    (get & openid) { id =>
      extractExecutionContext { implicit ec =>
        complete(handleGetCategorys(id))
      }
    }
  }

  def saveCategories = path("saveCategories") {
    (post & openid & entity(as[CategoriesRequest])) { (id, cats) =>

      // todo:
      // 1. 获取id对应的所有人脉, 如果人脉为空则
      //      redirect = 0,  为跳转到 分享
      //    如果人脉不为空,
      //      rediect = 1, 为跳转到体验页
      //
      //
      complete(success(CategoriesResponse(0, Map())))
    }
  }

  def saveComment(content: String, reportId: String, openid: String): Future[Result[Boolean]] =
    Future.successful(success(true))  // todo: 保存评论到mongodb

  def addComment = path("comment") {
    (post & entity(as[CommentRequest]) & openid) { (comment, openid) =>
      complete(saveComment(comment.content, comment.reportId, openid))
    }
  }

  // 获取未确认署名
  def getUnverifiedSignInfo = path("signCheck") {
    get {
      complete("ok")
    }
  }

  // 保存体验报告草稿
  def saveReport = path("report") {
    post {
      complete("ok")
    }
  }

  // 补充个人信息
  def addPersonInfo = path("person") {
    post {
      complete("ok")
    }
  }

  def tst = path("test") {
    get {
      openid { id =>
        optSession(id) { (session: Future[Option[Session]]) =>
          implicit val format = jsonFormat1(Session)
          complete(session)
        }
      }
    }
  }

  def miscRoute = addComment ~ getCategorys  ~ saveCategories ~ tst
}
