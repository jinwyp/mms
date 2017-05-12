package com.gongshijia.mms.user.login

import com.gongshijia.mms.{ApiSupport, Core}

import scala.concurrent.Future

/**
  * Created by hary on 2017/5/12.
  */
trait LoginController extends ApiSupport with Core with LoginService {

  import LoginModels._

  def handleLogin(request: LoginRequest): Future[LoginResponse] = {
    log.info("POST /login/login request: {}", request)
    createSession(request).map {
      (t: (Session, String)) => LoginResponse(t._2)
    }
  }

  //  def handleLogout(request: LogoutRequest): Future[LogoutResponse] = {
  //    Future.successful(LogoutResponse("ok", "ok"))
  //  }
  //
  //  def handleRegister(request: RegisterRequest): Future[RegisterResponse] = {
  //    Future.successful(RegisterResponse("ok", "ok"))
  //  }
}
