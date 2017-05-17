package com.gongshijia.mms.core

import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA1

/**
  * Created by hary on 2017/5/12.
  */
object Utils {
  // 获取上传文件签名
  def hmacSHA1(key: Array[Byte], data: Array[Byte]): String = {
    val signingKey: SecretKeySpec = new SecretKeySpec(key, HMAC_SHA1);
    val mac = Mac.getInstance("HmacSHA1");
    mac.init(signingKey);
    val rawHmac = mac.doFinal(data);
    base64Encode(rawHmac);
  }

  def base64Encode(origin: Array[Byte]): String = {
    if (null == origin) {
      return null;
    }
    new sun.misc.BASE64Encoder().encode(origin).replace("\n", "").replace("\r", "");
  }

}
