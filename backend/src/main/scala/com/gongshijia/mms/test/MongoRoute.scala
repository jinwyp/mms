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
trait MongoRoute extends Core with SprayJsonSupport {

  // mongo example
  def mongoInsert = path ("insert") {
    post {
      complete("ok")
    }
  }

  def mongoUpsert = path ("upsert") {
    post {
      complete("ok")
    }
  }

  def mongoDelete = path ("delete") {
    post {
      complete("ok")
    }
  }

  def mongoPager = path ("pager") {
    post {
      complete("ok")
    }
  }

  def mongoSelect = path("select") {
    get {
      complete("ok")
    }
  }

  def mongoRoute = mongoInsert ~ mongoUpsert ~ mongoDelete ~ mongoPager ~ mongoSelect
}
