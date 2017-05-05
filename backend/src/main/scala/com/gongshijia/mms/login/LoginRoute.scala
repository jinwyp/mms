package com.gongshijia.mms.login

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, onSuccess, path, post, _}
import com.gongshijia.mms.Core

/**
  * Created by hary on 2017/5/3.
  */
trait LoginRoute extends Core with SprayJsonSupport with Models{
  // 用户登录
  def login = path("login") {
    post {
      entity(as[LoginRequest]) { request =>
        log.info("POST /login/login request: {}", request)
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


