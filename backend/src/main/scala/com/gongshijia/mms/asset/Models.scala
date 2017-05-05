package com.gongshijia.mms.asset

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/2.
  */
trait Models extends DefaultJsonProtocol {

  case class PolicyResponse(callback: String, signature: String, policy: String, ossAccessId: String, host: String,dirName:String)
  implicit val PolicyResponseFormat = jsonFormat6(PolicyResponse)
}

object Models extends Models
