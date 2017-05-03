package com.gongshijia.mms.service

import akka.actor.{Actor, ActorLogging, Props, Terminated}
import akka.actor.Actor.Receive
import com.gongshijia.mms.service.UserActor.UserCommand


object UserMaster {
  def props: Props = Props(new UserMaster)

}

/**
  * Created by hary on 2017/5/3.
  */
class UserMaster extends Actor with ActorLogging {

  // 创建user actor
  def create(userId: String) = {
    context.actorOf(UserActor.props, userId)
  }

  override def receive: Receive = {
    case cmd: UserCommand =>
      val child = context.child(cmd.openid).fold(create(cmd.openid))(identity)
      child forward cmd
    case Terminated(child) =>
      log.info("{} terminated", child)
  }
}
