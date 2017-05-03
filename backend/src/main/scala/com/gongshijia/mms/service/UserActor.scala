package com.gongshijia.mms.service

import akka.actor.{ActorLogging, Props}
import akka.event.LoggingAdapter
import akka.persistence.{PersistentActor, RecoveryCompleted, SnapshotOffer}
import com.gongshijia.mms.service.UserActor.{UserEvent, UserState}

object UserActor {

  def props: Props = Props(new UserActor)

  // 命令
  trait UserCommand {
    def userId: String
  }

  // 用户事件
  trait UserEvent

  // 用户状态
  case class UserState(){
    def update(event: UserEvent): UserState = {
      null
    }
  }

}

/**
  * Created by hary on 2017/5/3.
  */
class UserActor extends PersistentActor with ActorLogging {

  override def persistenceId: String = self.path.name

  var state: UserState = UserState()

  def logState(mark: String = "") = {
    log.info("<{}>  state = {}", mark, state)
  }

  override def receiveRecover: Receive = {
    case ev: UserEvent =>
      log.info(s"recover with event: $ev")
      state = state.update(ev)

    case SnapshotOffer(_, snapshot: UserState) =>
      state = snapshot
      log.info(s"snapshot recovered")

    case RecoveryCompleted =>
      logState("recovery completed")
  }

  // 收到命令
  override def receiveCommand: Receive = {

    case _ =>
  }

}
