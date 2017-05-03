package com.gongshijia.mms

import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.HttpRequest
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server._
import akka.http.scaladsl.server.directives.LogEntry
import akka.util.Timeout
import com.gongshijia.mms.asset.AssetRoute
import com.gongshijia.mms.login.LoginRoute

/**
  * Created by hary on 2017/5/2.
  */
object mmsApp extends App
  with MmsExceptionHandler
  with MmsRejectionHandler
  with AssetRoute
  with LoginRoute {

  val config = coreSystem.settings.config

  def extractLogEntry(req: HttpRequest): RouteResult => Option[LogEntry] = {
    case RouteResult.Complete(res) => Some(LogEntry(req.method.name + " " + req.uri + " => " + res.status, Logging.InfoLevel))
    case _ => None // no log entries for rejections
  }

  val route: Route = logRequestResult(extractLogEntry _) {
    pathPrefix("asset") {
      assetRoute
    }
    pathPrefix("login") {
      loginRoute
    }
  }


  // start http server
  implicit val httpExecutionContext = coreSystem.dispatcher
  println(s"http is listening on ${config.getInt("http.port")}")
  Http().bindAndHandle(route, "0.0.0.0", config.getInt("http.port"))

}
