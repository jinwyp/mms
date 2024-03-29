package com.gongshijia.mms.category

import com.gongshijia.mms.core.Neo4jSupport
import com.gongshijia.mms.experience.ExperienceReportController
import com.gongshijia.mms.user.login.LoginService

import scala.async.Async.{async, await}
import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryController extends CategoryService with LoginService with Neo4jSupport with ExperienceReportController {

  import CategoryModels._

  def handlerUserCategoriesGet(openid: String)(implicit ec: ExecutionContext): Future[CategoriesResponse] = {

    findUserByOpenId(openid).map { u =>
      if (u.categories == Nil) {
        CategoriesResponse(defaultCategories)
      } else {
        val arts = defaultCategories.map(a =>
          if (u.categories.contains(a.name)) {
            a.copy(checked = true)
          } else {
            a
          }
        )
        CategoriesResponse(arts)
      }
    }.recover{case _=>CategoriesResponse(defaultCategories)}
  }

  //添加关注的类目
  def handlerCategoriesToUser(openid: String, categoriesStr: List[String]): Future[CategoriesResponse] = {
    val categories = defaultCategories.filter(a => categoriesStr.contains(a.name)).map(_.name)
    async {
      if (await(addCategoriesToUser(openid, categories))) {
        if (await(findFriendReport(openid, 10, 0)).length > 0) {
          // 到好友页面
          CategoriesResponse(defaultCategories, Some(1))
        } else {
          //到收藏页面
          CategoriesResponse(defaultCategories, Some(0))
        }
      } else {
        CategoriesResponse(defaultCategories, Some(0))
      }
    }
  }

}