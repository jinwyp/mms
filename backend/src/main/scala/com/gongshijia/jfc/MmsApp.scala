package com.gongshijia.mms

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.misc.MiscRoute
import com.gongshijia.mms.user.UserRoute

/**
  * Created by hary on 2017/5/12.
  */
trait AllSupport extends App
  with Core
  with ApiSupport
  with HttpSupport
  with ExceptionSupport
  with MongoSupport
  with RedisSupport
  with SprayJsonSupport

trait AppRoute extends MiscRoute with UserRoute

object MmsApp extends AllSupport
  with AppRoute {

  val route: Route =
    path("hello") {
      get {
        complete("hello")
      }
    } ~
  pathPrefix("misc") { miscRoute } ~
  pathPrefix("user") { userRoute }

  startHttp(route)
}
