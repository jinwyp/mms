package com.gongshijia.mms.category

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryController extends CategoryService {

  import CategoryModels._


  def handlerUserCategoriesGet(openid: String)(implicit ec: ExecutionContext): Future[CategoriesResponse] = {
    findCategoriesForUser(openid) map {
      case Some(categoryStr) => {
        val arts: List[Category] = defaultCategories.map(a =>
          if (categoryStr.contains(a.name)) {
            a.copy(checked = true)
          } else {
            a
          }
        )
        CategoriesResponse(arts)
      }
      case _ => {
        CategoriesResponse(defaultCategories)
      }
    }
  }

  //添加关注的类目
  def handlerCategoriesToUser(openid: String, categoriesStr: List[String]): Future[Boolean] = {
    val categories = defaultCategories.filter(a => categoriesStr.contains(a.name)).map(_.name)
    addCategoriesToUser(openid, categories)
  }
}