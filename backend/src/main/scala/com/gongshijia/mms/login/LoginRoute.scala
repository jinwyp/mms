package com.gongshijia.mms.login

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
import javax.crypto.Mac
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

import Models._

/**
  * Created by hary on 2017/5/3.
  */
trait LoginRoute extends Core with SprayJsonSupport {


  val appId = coreSystem.settings.config.getString("wx.appId")
  val appSecret = coreSystem.settings.config.getString("wx.appSecret")

  implicit val ec = coreSystem.dispatcher

  // 获取WxSession
  def getWxSession(code: String): Future[WxSession] = {

    import spray.json._


    val wxUrl = s"https://api.weixin.qq.com/sns/jscode2session?appid=${appId}&secret=${appSecret}&js_code=${code}&grant_type=authorization_code"

    for {
      resposne <- Http().singleRequest(HttpRequest(uri = wxUrl, method = HttpMethods.GET))
      entity <- resposne.entity.toStrict(10.seconds)
      session <- entity.dataBytes.runFold(ByteString.empty) { case (acc, b) => acc ++ b } map {
        _.decodeString("UTF-8").parseJson.convertTo[WxSession]
      }
    } yield session
  }

  // 保存session
  def saveSession(wxSession: WxSession): Future[Option[String]] = {
    import java.util.UUID
    val sid = UUID.randomUUID().toString
    redis.set(sid, wxSession) map {
      case true => Some(sid)
      case _ => None
    }
  }

  // 处理登录
  def handleLogin(code: String) = {
    for {
      wxSession <- getWxSession(code)
      success <- saveSession(wxSession)
    } yield success
  }

  // 用户登录
  def login = path("login") {
    get {
      parameter('code) { code =>
        onSuccess(handleLogin(code)) {
          case Some(sid) =>
            complete(LoginResponse(sid))
        }
      }
    }
  }

  // 获取类目
  def getArts = path("arts") {
    get {
      complete("ok")
    }
  }

  // 添加评价
  def addComment = path("comment") {
    wxSession { (fs: Future[Option[WxSession]]) =>
      put {
        complete("ok")
      }
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

  //

  def loginRoute = login

}
