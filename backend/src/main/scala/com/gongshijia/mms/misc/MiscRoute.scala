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
trait MiscRoute extends SprayJsonSupport with Core {

  // 获取类目
  case class ArtResult(name: String, checked: Boolean = false)

  implicit val ArtResultFormat = jsonFormat2(ArtResult)
  val artsList: Seq[ArtResult] = List(
    ArtResult("造型"),
    ArtResult("美甲"),
    ArtResult("茶艺"),
    ArtResult("插花"),
    ArtResult("搭配"),
    ArtResult("健身")
  )

  def handleGetArts(openid: String)(implicit ec: ExecutionContext) = {
    // todo:  依据openid 从mongo中拿出这个人的兴趣
    val fis: Future[List[String]] = Future.successful(List("造型")) // 获取兴趣
    fis map { is =>
      val arts: Seq[ArtResult] = artsList.map { art =>
        if (is.contains(art.name)) {
          art.copy(checked = true)
        } else {
          art
        }
      }
      success(arts)
    }
  }

  def getArts = path("arts") {
    (get & headerValueByName("X-OPENID")) { openid =>
      extractExecutionContext { implicit ec =>
        complete(handleGetArts(openid))
      }
    }
  }

  // 添加评价
  case class CommentRequest(content: String)
  implicit val CommentFormat = jsonFormat1(CommentRequest)
  def saveComment(content: String, openid: String): Future[Result[Boolean]] = Future.successful(success(true))  // todo: 保存评论到mongodb
  def addComment = path("comment") {
    (post & entity(as[CommentRequest]) & openid) { (comment, openid) =>
      complete(saveComment(comment.content, openid))
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

  def miscRoute = addComment ~ getArts  ~ tst
}
