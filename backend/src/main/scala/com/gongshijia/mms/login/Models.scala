package com.gongshijia.mms.login

import akka.util.ByteString
import redis.ByteStringFormatter
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/3.
  */
object Models extends DefaultJsonProtocol {
  import spray.json._

  case class WxSession(openid: String, session_key: String, expires_in: Long)
  implicit val WxSessionFormat = jsonFormat3(WxSession)

  // for redis
  implicit val byteStringFormatter = new ByteStringFormatter[WxSession] {
    override def serialize(data: WxSession): ByteString = {
      ByteString(data.toJson.toString())
    }
    override def deserialize(bs: ByteString): WxSession = {
      bs.utf8String.toJson.convertTo[WxSession]
    }
  }

  case class LoginResponse(sid: String)
  implicit val LoginResponseFormat = jsonFormat1(LoginResponse)

}
