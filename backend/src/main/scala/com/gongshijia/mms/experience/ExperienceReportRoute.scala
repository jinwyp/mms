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
    (id, report) => {
      complete(addReport(id, report).toResult)
    }
  }

  //署名体验报告
  def signReport = (path("signReport") & post & openid & entity(as[SignInfoRequest])) {
    (id, signInfo) => {
      complete(addSignReport(id,signInfo).toResult)
    }
  }

  //添加评论
  def addComment = (path("addComment") & post & openid & entity(as[CommentsRequest])) {
    (id, comments) => {
      complete(addComments(id, comments).toResult)
    }
  }

  //加载好友体验报告
  def loadFriendReport = get { (path("loadFriendReport") & openid & parameter("pageSize".as[Int]) & parameter("page".as[Int])) { (openid, pageSize, page) =>
      complete(listReport(openid, pageSize, page,null).toResult)
    }
  }


  //加载收藏体验报告
  def loadCollectReport = get { (path("loadCollectReport") & openid & parameter("pageSize".as[Int]) & parameter("page".as[Int]) & parameter("category".as[String])) { (openid, pageSize, page, category) =>
      complete(listReport(openid, pageSize, page, category).toResult)
    }
  }

  /**
    * 打开分享的体验报告
    * GET: /report/{reportid}/{from}
    *
    * @return
    */
  def openShareReport = get {
    (path("shareReport" / Segment / Segment) & openid) { (reportid, fromOpenId,currentOpenId) =>
      complete(findByIdAndOpenid(fromOpenId=fromOpenId,currentOpenId =currentOpenId,expId = reportid).toResult)
    }
  }


  /**
    * 打开体验报告
    * GET: /report/{reportid}
    *
    * @return
    */
  def openReport = get {
    path(Segment) { (reportid) =>
      complete(findById(reportid).toResult)
    }
  }

  /**
    * 加载所有报告记录
    *
    * @return
    */
  def loadAllPage = get {
    (path("list") & openid & parameter("pageSize".as[Int]) & parameter("page".as[Int]) & parameter("category".as[String])) { (openid, pageSize, page, category) =>
      complete(listReport(openid, pageSize, page, category).toResult)
    }
  }

  def experienceReportRoute: Route = loadFriendReport ~ loadCollectReport ~ releaseReport ~ openShareReport ~ loadAllPage ~ signReport ~ addComment ~ openReport
}
