package com.gongshijia.mms.misc

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.gongshijia.mms.misc.address.AddressRoute

/**
  * Created by hary on 2017/5/12.
  */
trait MiscRoute extends SprayJsonSupport with AddressRoute {

  def miscHello = get {
    path("hello") {
      complete("hello is ok")
    }
  }

  def miscRoute = pathPrefix("address") {
    addressRoute
  }

}
