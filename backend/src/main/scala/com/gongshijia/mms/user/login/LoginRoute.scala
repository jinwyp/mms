package com.gongshijia.mms.user.login

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.core.HttpSupport

/**
  * Created by hary on 2017/5/12.
  */
trait LoginRoute extends HttpSupport with LoginController {

  import LoginModels._

  // 处理登录
  def loginLogin = (path("login") & post & entity(as[LoginRequest])) { req =>
      complete(handleLogin(req).toResult)
  }

  def loginRoute: Route = loginLogin
}

