package com.gongshijia.mms.service

import akka.actor.ActorRef

/**
  * Created by hary on 2017/5/3.
  */
trait ServiceActors {
  def userMaster: ActorRef
}
