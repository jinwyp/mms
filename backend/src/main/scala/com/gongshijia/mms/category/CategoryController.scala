package com.gongshijia.mms.category

import scala.concurrent.{Await, ExecutionContext, Future}
import  scala.concurrent.duration._

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryController extends CategoryService {

  import CategoryModels._

  def handleCategoryGet(openid: String)(implicit ec: ExecutionContext): Future[CategoriesResponse] = {
    val userCategory: Future[Option[List[String]]] = findCategoriesForUser(openid);
    userCategory map { is =>
      val arts: Seq[Category] = defaultCategories.map { art =>
        if (is.contains(art.name)) {
          art.copy(checked = true)
        } else {
          art
        }
      }
      CategoriesResponse(arts)
    }
  }
}
