package com.gongshijia.mms.asset

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/2.
  */
trait AssetModels extends DefaultJsonProtocol {

  case class PolicyResponse(callback: String, signature: String, policy: String, ossAccessId: String, host: String,dirName:String)
  implicit val PolicyResponseFormat = jsonFormat6(PolicyResponse)

  case class OSSResponse(filename:String,mimeType:String)
  implicit val OSSResponseFormat = jsonFormat2(OSSResponse)
}

object AssetModels extends AssetModels
