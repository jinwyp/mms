package com.gongshijia.mms.user.login

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/12.
  */
object LoginModels extends DefaultJsonProtocol {
  // 登录
  case class LoginRequest(code: String,
                          avatarUrl: String,
                          country: String,
                          province: String,
                          city: String,
                          gender: Int,
                          language: String,
                          nickName: String,
                          rawData: String,
                          signature: String,
                          encryptedData: String,
                          iv: String)
  case class LoginResponse(openid: String)
  implicit val LoginRequestFormat = jsonFormat12(LoginRequest)
  implicit val LoginResponseFormat = jsonFormat1(LoginResponse)
}
