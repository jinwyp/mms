package com.gongshijia.mms.category

import com.gongshijia.mms.user.login.LoginService

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryController extends CategoryService with LoginService {

  import CategoryModels._

  def handlerUserCategoriesGet(openid: String)(implicit ec: ExecutionContext): Future[CategoriesResponse] = {

    findUserByOpenId(openid) map { u =>
      if (u.categories == null || u.categories.size==0) {
        CategoriesResponse(defaultCategories)
      } else {
        val arts = defaultCategories.map(a =>
          if (u.categories.get.contains(a.name)) {
            a.copy(checked = true)
          } else {
            a
          }
        )
        CategoriesResponse(arts)
      }
    }
  }

  //添加关注的类目
  def handlerCategoriesToUser(openid: String, categoriesStr: List[String]): Future[Boolean] = {
    val categories = defaultCategories.filter(a => categoriesStr.contains(a.name)).map(_.name)
    addCategoriesToUser(openid, categories)
  }
}