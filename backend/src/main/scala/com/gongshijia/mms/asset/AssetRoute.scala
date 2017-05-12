package com.gongshijia.mms.asset

import akka.http.scaladsl.server.Directives.{complete, get, path, _}
import com.gongshijia.mms.core.{AppConfig, Core, HttpSupport}

/**
  * Created by hary on 2017/5/2.
  */
trait AssetRoute extends Core with HttpSupport with AssetController {

  // GET /asset/policy
  def assetUploadPolicy = (path("policy") & get & openid) { id =>
    complete(success(handleUploadPolicy(id)))
  }

  // 阿里云上传成功回调 ---应答给---> 阿里云 ---> 浏览器
  def assetOssCallback = (path("callback") & formFields("filename", "mimeType")) { (filename, mimeType) =>
    complete(handleOssCallback(filename, mimeType))
  }

  def assetRoute = assetUploadPolicy ~ assetOssCallback

}


