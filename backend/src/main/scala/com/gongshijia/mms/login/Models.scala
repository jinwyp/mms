package com.gongshijia.mms.login

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/3.
  */
trait Models extends DefaultJsonProtocol {


  case class LoginResponse(openid: String)

  implicit val LoginResponseFormat = jsonFormat1(LoginResponse)

}

object Models extends Models