package com.gongshijia.mms.test


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.gongshijia.mms.Core

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

/**
  * Created by hary on 2017/5/3.
  */
trait RedisRoute extends Core with SprayJsonSupport {

  // redis example
  def redisSet = path("set") {
    post {
      complete("ok")
    }
  }

  def redisGet = path("get") {
    complete("ok")
  }

  def redisRoute = redisGet ~ redisSet

}
