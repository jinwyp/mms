package com.gongshijia.mms.user

import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import com.gongshijia.mms.core.HttpSupport
import com.gongshijia.mms.user.login.LoginRoute

/**
  * Created by hary on 2017/5/12.
  */
trait UserRoute extends HttpSupport with LoginRoute {

  def userRoute: Route = pathPrefix("login") {
    loginRoute
  }
}
