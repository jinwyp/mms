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
import scalaz._
import Scalaz._

/**
  * Created by hary on 2017/5/3.
  */
trait LoginRoute extends Core with SprayJsonSupport with Models{

  // 用户登录
  def login = path("login") {
    post {
      entity(as[LoginRequest]) { request =>

        log.info(request.toJson.prettyPrint)

        log.info("POST /login/login request: {}", request)

//        val f1 = Future.successful(1)
//        val f2 = Future.successful(1L)
//
//        val lf = List(Future.successful(1), Future.successful(2))
//
//        val fl: Future[List[Int]] = Future.sequence(lf)
//
//        val jobs = List(1,2,3)   // 1 -> 2  3
//
//        val fj: Future[List[Int]] = Future.traverse(jobs){ i =>  Future { i * i }}
//
//
//        val f3 = (f1 |@| f2 |@| fl) { (a, b, c) =>
//          1
//        }



        val fs = createSession(request) // 创建session

        onSuccess(fs) {
          case (session, openid) =>
            log.info("session created: openid = {}, session = {}", openid, session)
            complete(success(LoginResponse(openid)))
        }
      }
    }
  }

  def loginRoute = login

}


