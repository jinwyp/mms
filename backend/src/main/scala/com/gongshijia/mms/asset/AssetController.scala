package com.gongshijia.mms.asset

import com.gongshijia.mms.Utils

/**
  * Created by hary on 2017/5/12.
  */
trait AssetController extends AssetService {

  import AssetModels._

  def handleUploadPolicy(openid: String): PolicyResponse = {
    val policy = Utils.base64Encode(getPolicy.getBytes());
    PolicyResponse(
      AssetServiceUtils.callback,
      Utils.hmacSHA1(accessKeySecret.getBytes(), policy.getBytes()),
      policy,
      accessKeyId,
      ossHost,
      openid)
  }

}
