package com.gongshijia.mms.misc.address

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._

/**
  * Created by hary on 2017/5/12.
  */
trait AddressRoute extends SprayJsonSupport {

  // 返回省份列表
  def addressProvinces = get {
    path("provinces") {
      complete("ok")
    }
  }

  // 返回指定省份的地级市
  def addressCities = get {
    path("province" / Segment ) { province =>
      complete(province)
    }
  }

  // 返回指定地级市的县
  def addressCounties = get {
    path("province" / Segment / "city" / Segment ) { (province, city) =>
      complete(s"$province + $city")
    }
  }

  def addressRoute = addressProvinces ~ addressCities ~ addressProvinces
}
