package com.gongshijia.mms.experience

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.core.{HttpSupport, Neo4jSupport}
import com.gongshijia.mms.experience.ExperienceReportModels.{CommentsRequest, ExperienceReportRequest, SignInfoRequest}

import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportRoute extends ExperienceReportController with HttpSupport with Neo4jSupport {


  //发布体验报告
  def releaseReport = (path("releaseReport") & post & openid & entity(as[ExperienceReportRequest])) {
    (id, report) => complete(addReport(id, report).toResult)
  }

  //署名体验报告
  def signReport = (path("signReport") & post & openid & entity(as[SignInfoRequest])) {
    (id, signInfo) => complete(addSignReport(id, signInfo).toResult)
  }

  //添加评论
  def addComment = (path("addComment") & post & openid & entity(as[CommentsRequest])) {
    (id, comments) => complete(addComments(id, comments).toResult)
  }

  //加载好友体验报告
  def loadFriendReport = get {
    (path("loadFriendReport") & openid & parameter("pageSize".as[Int]) & parameter("page".as[Int])) {
      (openid, pageSize, pageNum) => {
        val rs  = findFriendReport(openid = openid, pageSize = pageSize, pageNum = pageNum).map{ reports=>
          reports.map{ t=>
            if(t.checked ==0 && t.openid == openid){
              t
            }else{
              t.copy(signInfo = Nil)
            }
          }
        }
        complete(rs.toResult)
      }
    }
  }

  //加载体验报告详情
  def openReport = get {
    (path("report" / Segment) & openid) {
      (reportId, openId) => {
        val rs = findById(reportId,openId).map {
          case t: ExperienceReportResponse if (t.checked == 0) => t.copy(signInfo = Nil)
          case t:ExperienceReportResponse  if (t.checked == 1) => t.copy(signInfo = t.signInfo.filter(_.checked == 1))
        }
        complete(rs.toResult)
      }
    }
  }

  /**
    * 创建好友关系
    * GET: /report/createSharedRelationShip/{reportid}/{from}
    *
    * @return
    */
  def handlerSharedRelationShip = get {
    (path("createSharedRelationShip" / Segment / Segment) & openid) {
      (reportId, fromOpenId, currentOpenId) => {
        complete(createFriendRelationShip(fromOpenId, currentOpenId, reportId).toResult)
      }
    }
  }


  //加载确认署名信息
  def loadMakeSureReport = get {
    (path("makeSureReport") & openid & parameter("reportId".as[String]) & parameter("signInfoId".as[String])) {
      (openId, reportId, signInfoId) => {
        val rs = findByIdAndOpenId(openId, reportId).map(report => {
          report.copy(signInfo = report.signInfo.filter(s => s._id.toString.equals(signInfoId)))
        })
        complete(rs.toResult)
      }
    }
  }

  //处理署名请求
  def handlerMakeSureReport = get {
    (path("handlerMakeSureReport") & openid & parameter("reportId".as[String]) & parameter("signInfoId".as[String])) { (openid, reportId, signInfoId) =>
      complete(makeSureSignInfo(openid, reportId, signInfoId).toResult)
    }
  }

  //加载收藏体验报告
  def loadCollectReport = get {
    (path("loadCollectReport") & openid & parameter("category".as[String])) {
      (openid, category) => complete(findCollectReport(openid, category).toResult)
    }
  }

  //添加到收藏
  def addCollect = (path("addReportToCollect") & get & openid & parameter("reportId".as[String])) {
    (openId, reportId) => complete(addReportToCollect(openId, reportId).toResult)
  }

  //移除收藏
  def removeCollect = (path("removeCollectReport") & get & openid & parameter("reportId".as[String])) {
    (openId, reportId) => complete(removeCollectReport(openId, reportId).toResult)
  }


  def experienceReportRoute: Route = loadFriendReport ~ loadCollectReport ~ releaseReport ~ signReport ~ addComment ~ openReport ~ loadMakeSureReport ~ handlerMakeSureReport ~ addCollect ~ removeCollect ~ handlerSharedRelationShip
}


