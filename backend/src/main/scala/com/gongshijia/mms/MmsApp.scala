package com.gongshijia.mms

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.asset.AssetRoute
import com.gongshijia.mms.core._
import com.gongshijia.mms.misc.MiscRoute
import com.gongshijia.mms.user.UserRoute
import com.gongshijia.mms.category.CategoryRoute

/**
  * Created by hary on 2017/5/12.
  */
trait AppRoute extends AssetRoute  with UserRoute with CategoryRoute

object MmsApp extends AllSupport with AppRoute {
  val route: Route =
    path("hello") {
      get {
        complete("hello")
      }
    } ~
  pathPrefix("asset") { assetRoute} ~
  pathPrefix("user") { userRoute } ~
  pathPrefix("category"){categoryRoute}

  startHttp(route)
}
