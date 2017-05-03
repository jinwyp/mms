package com.gongshijia.mms.service

import akka.actor.ActorRef

import akka.pattern._
import akka.util.Timeout
import scala.concurrent.duration._

import scala.concurrent.Future

/**
  * Created by hary on 2017/5/3.
  */
trait UserService {
  val userMaster: ActorRef

  implicit val gTimeout = Timeout(5.seconds)

  // 用户登录
  def login(openid: String) = {
    (userMaster ? openid).mapTo[Int]
  }
}
