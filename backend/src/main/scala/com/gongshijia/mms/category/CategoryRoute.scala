package com.gongshijia.mms.category

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.gongshijia.mms.category.CategoryModels.{CategoriesRequest, Category}
import com.gongshijia.mms.core.HttpSupport

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryRoute extends CategoryController with HttpSupport {


  def categoryGet = (path("category") & get & openid) { id =>
    complete(handlerUserCategoriesGet(id).toResult)
  }

  def saveCategories = (path("saveCategories") & post & openid & entity(as[CategoriesRequest])) { (id,categoriesReq) => {
          complete(handlerCategoriesToUser(id, categoriesReq.categories).toResult)
    }
  }

  def categoryRoute: Route = categoryGet ~ saveCategories
}
