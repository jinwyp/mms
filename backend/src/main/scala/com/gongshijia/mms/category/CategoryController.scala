package com.gongshijia.mms.category

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryController extends CategoryService {

  import CategoryModels._

  def handleCategoryGet(openid: String)(implicit ec: ExecutionContext): Future[CategoriesResponse] = {
    val userCategory: Future[Option[List[String]]] = findCategoriesForUser(openid)
    userCategory map { is =>
      val arts: Seq[Category] = defaultCategories.map { art =>
        if (art.name.contains(is)) {
          art.copy(checked = true)
        } else {
          art
        }
      }
      CategoriesResponse(arts)
    }
  }
}
