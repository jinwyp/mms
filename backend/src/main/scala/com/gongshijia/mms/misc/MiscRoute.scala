package com.gongshijia.mms.misc


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.gongshijia.mms.Core
import com.gongshijia.mms.service.UserService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/4.
  */
trait MiscRoute extends SprayJsonSupport with Core with Models with UserService {

  def handleGetCategorys(openid: String)(implicit ec: ExecutionContext): Future[Result[Seq[Category]]] = {
    val userCategory: Future[Option[List[String]]] = findArtsList(openid)
        userCategory map { is =>
          val arts: Seq[Category] = artsList.map { art =>
            if (art.name.contains(is)) {
              art.copy(checked = true)
            } else {
              art
            }
          }
          success(arts)
        }


  }

  def getCategorys = path("arts") {
    (get & openid) { id =>
      extractExecutionContext { implicit ec =>
        complete(handleGetCategorys(id))
      }
    }
  }

  def handleSaveCategories(id: String, cats: CategoriesRequest) = {


  }

  def saveCategories = path("saveCategories") {
    (post & openid & entity(as[CategoriesRequest])) { (id, cats) =>
      onSuccess(addArtsList(id, cats.categories)) {
        case _ => complete(success(CategoriesResponse(1, null)))
      }
      // todo:
      // 1. 获取id对应的所有人脉, 如果人脉为空则

      //      redirect = 0,  为跳转到 分享
      //    如果人脉不为空,
      //      rediect = 1, 为跳转到体验页
      //
      //
    }
  }

  def saveComment(content: String, reportId: String, openid: String): Future[Result[Boolean]] =
    Future.successful(success(true)) // todo: 保存评论到mongodb

  def addComment = path("comment") {
    (post & entity(as[CommentRequest]) & openid) { (comment, openid) =>
      complete(saveComment(comment.content, comment.reportId, openid))
    }
  }

  // 获取未确认署名
  def getUnverifiedSignInfo = path("signCheck") {
    get {
      complete("ok")
    }
  }

  // 保存体验报告草稿
  def saveReport = path("report") {
    post {
      complete("ok")
    }
  }

  // 补充个人信息
  def addPersonInfo = path("person") {
    post {
      complete("ok")
    }
  }

  def tst = path("test") {
    get {
      openid { id =>
        optSession(id) { (session: Future[Option[Session]]) =>
          implicit val format = jsonFormat1(Session)
          complete(session)
        }
      }
    }
  }

  def miscRoute = addComment ~ getCategorys ~ saveCategories ~ tst
}
