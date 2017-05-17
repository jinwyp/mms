package com.gongshijia.mms.user

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.core.HttpSupport
import com.gongshijia.mms.user.login.LoginModels.UserUpdateRequest
import com.gongshijia.mms.user.login.{LoginRoute, LoginService}

/**
  * Created by hary on 2017/5/12.
  */
trait UserRoute extends HttpSupport with LoginRoute with LoginService {

  def login: Route = pathPrefix("login") {
    loginRoute
  }

  def loadUserInfo = (path("loadUserInfo") & get & openid) {
    id => complete(findUserByOpenId(id))
  }

  def improveInfo= (path("improveInfo") & post & openid & entity(as[UserUpdateRequest])) {
    (id,user) => complete(updateUserInfo(id,user).toResult)
  }
  def userRoute: Route = login ~ loadUserInfo ~ improveInfo
}
