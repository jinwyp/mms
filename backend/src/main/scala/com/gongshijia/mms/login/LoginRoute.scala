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


import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import org.apache.commons.codec.binary.Base64;
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1;

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.gongshijia.mms.Core

/**
  * Created by hary on 2017/5/3.
  */
trait LoginRoute extends Core with SprayJsonSupport {

  // 用户登录
  def login = path("login") {
    get {
      complete("ok")
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
    put {
      complete("ok")
    }
  }

  // 获取未确认署名
  def getUnverifiedSignInfo = path("signCheck") {
    get {
      complete("ok")
    }
  }

  // 保存体验报告草稿
  def saveReport =  path("report") {
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
