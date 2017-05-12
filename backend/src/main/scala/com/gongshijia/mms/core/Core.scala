package com.gongshijia.mms.core

import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer

/**
  * Created by hary on 2017/5/12.
  */
trait Core {
  protected def mkSystem: ActorSystem = ActorSystem("mms-system")

  // 系统， 配置， 日志, 线程池
  implicit val coreSystem: ActorSystem = mkSystem
  implicit val coreMaterializer = ActorMaterializer()
  implicit val coreExecutionContext = coreSystem.dispatcher
  val coreConfig = coreSystem.settings.config
  val log = Logging(coreSystem, this.getClass)

  //
  case class Session(session_key: String)

}


trait AllSupport extends App
  with Core
  with ApiSupport
  with HttpSupport
  with ExceptionSupport
  with MongoSupport
  with RedisSupport
  with SprayJsonSupport
