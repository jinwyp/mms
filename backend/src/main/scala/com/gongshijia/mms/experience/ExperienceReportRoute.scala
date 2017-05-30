package com.gongshijia.mms.experience

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.core.HttpSupport
import com.gongshijia.mms.experience.ExperienceReportModels.{CommentsRequest, ExperienceReportRequest, SignInfoRequest}

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportRoute extends ExperienceReportController with HttpSupport {


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
          val rs = findFriendReport(openid = openid, pageSize = pageSize, pageNum = pageNum) map { reports=>
          reports.map( t =>
            if(t.checked==0 && t.openid==openid){
              t
            }else if(t.checked==1){
              t.copy(signInfo=t.signInfo.filter(_.checked==1))
            }else{
              t.copy(signInfo = Nil)
            }
          )
        }
        complete(rs.toResult)
      }
    }
  }


  /**
    * 打开分享的体验报告
    * GET: /report/shareReport/{reportid}/{from}
    *
    * @return
    */
  def openShareReport = get {
    (path("shareReport" / Segment / Segment) & openid) {
      (reportId, fromOpenId, currentOpenId) => complete(findByIdAndOpenId(fromOpenId = fromOpenId, currentOpenId = currentOpenId, reportId = reportId).toResult)
    }
  }


  /**
    * 打开体验报告
    * GET: /report/{reportid}
    *
    * @return
    */
  def openReport = get {
    (path("report" / Segment) & openid) {
      (reportId,openId) => {
        val rs =findById(reportId).map(report=>{
          if(report.checked==1){
            report.copy(signInfo=report.signInfo.filter(s=>s.checked==1).map(t=>t))
          }else{
            if(report.openid==openId){
              report
            }else{
              report.copy(signInfo = Nil)
            }
          }})
        complete(rs.toResult)
      }
    }
  }

  //加载确认署名信息
  def loadMakeSureReport= get {
    (path("makeSureReport") & openid & parameter("reportId".as[String]) & parameter("signInfoId".as[String])) {
      (openId, reportId, signInfoId) => {
        val rs =findByIdAndOpenId(openId,reportId).map(report=>{
          report.copy(signInfo=report.signInfo.filter(s=>s._id.toString.equals(signInfoId)).map(t=>t))
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


    def experienceReportRoute: Route = loadFriendReport ~ loadCollectReport ~ releaseReport ~ openShareReport ~ signReport ~ addComment ~ openReport ~ loadMakeSureReport ~ handlerMakeSureReport ~ addCollect ~ removeCollect
  }


