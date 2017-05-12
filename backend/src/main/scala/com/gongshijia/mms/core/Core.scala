package com.gongshijia.mms.core

import akka.actor.ActorSystem
import akka.event.Logging
import akka.stream.ActorMaterializer

/**
  * Created by hary on 2017/5/12.
  */
trait Core {
  protected def mkSystem: ActorSystem = ActorSystem("jfc-system")

  // 系统， 配置， 日志, 线程池
  implicit val coreSystem: ActorSystem = mkSystem
  implicit val coreMaterializer = ActorMaterializer()
  implicit val coreExecutionContext = coreSystem.dispatcher
  val coreConfig = coreSystem.settings.config
  val log = Logging(coreSystem, this.getClass)

  //
  case class Session(session_key: String)

}
