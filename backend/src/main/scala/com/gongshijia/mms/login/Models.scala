package com.gongshijia.mms.login

import akka.util.ByteString
import redis.ByteStringFormatter
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/3.
  */
trait Models extends DefaultJsonProtocol {

  case class LoginRequest(
        code: String,
        avatarUrl: String,
        country: String,
        province: String,
        city: String,
        gender: Int,
        language: String,
        nickName: String,
        rawData: String,
        iv: String
  )
  implicit val LoginRequestFormat = jsonFormat10(LoginRequest)

  case class LoginResponse(openid: String)
  implicit val LoginResponseFormat = jsonFormat1(LoginResponse)

}

object Models extends Models