package com.gongshijia.mms.service

import akka.actor.ActorRef
import akka.pattern._
import akka.util.Timeout

import scala.concurrent.Future
import scala.concurrent.duration._

/**
  * Created by hary on 2017/5/3.
  */
trait MMSService extends UserService {
  val userMaster: ActorRef

  implicit val timeoutxxx = Timeout(3.seconds)

//  def sendMessage(to: String, msg: String): Future[String] =
//    (userMaster ? 1 ).mapTo[String]

  //
  //
  //  利用后台的actor体系， 提供产生future的服务

}
