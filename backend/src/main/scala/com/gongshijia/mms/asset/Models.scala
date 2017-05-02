package com.gongshijia.mms.asset

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/2.
  */
object Models extends DefaultJsonProtocol {

  case class PolicyResponse(callback: String, signature: String, policy: String, ossAccessId: String)
  implicit val PolicyResponseFormat = jsonFormat4(PolicyResponse)

}
