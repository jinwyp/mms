package com.gongshijia.mms.login

import akka.util.ByteString
import redis.ByteStringFormatter
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/3.
  */
object Models extends DefaultJsonProtocol {

  case class WxSession(openid: String, session_key: String, expires_in: Long)
  implicit val WxSessionFormat = jsonFormat3(WxSession)

  case class LoginResponse(sid: String)
  implicit val LoginResponseFormat = jsonFormat1(LoginResponse)

  // for redis
  implicit val byteStringFormatter = new ByteStringFormatter[WxSession] {

    override def serialize(data: WxSession): ByteString = {
      ByteString(s"${data.openid}|${data.session_key}|${data.expires_in}")
    }
    override def deserialize(bs: ByteString): WxSession = {
      val ss = bs.utf8String.split("\\|")
      println()
      WxSession(ss(0), ss(1), ss(2).toLong)
    }
  }

}