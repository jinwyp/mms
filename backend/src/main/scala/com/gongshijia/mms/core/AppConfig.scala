package com.gongshijia.mms.core

/**
  * Created by hary on 2017/5/12.
  */
trait AppConfig extends Core {
  val accessKeyId = coreConfig.getString("aliyun.accessKeyId")
  val accessKeySecret = coreConfig.getString("aliyun.accessKeySecret")
  val ossBucket = coreConfig.getString("aliyun.ossBucket")
  val ossHost = coreConfig.getString("aliyun.ossHost")
  val mmsDomain = coreConfig.getString("mms.domain")

  //
  val appId = coreConfig.getString("wx.appId")
  val appSecret = coreConfig.getString("wx.appSecret")

  val fileRoot= coreConfig.getString("localoss.root")
  val localossHost= coreConfig.getString("localoss.ossHost").concat("asset/static/")

}
