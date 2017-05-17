package com.gongshijia.mms.category

import akka.http.scaladsl.server.Directives._
import com.gongshijia.mms.core.HttpSupport

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryRoute extends CategoryController with HttpSupport {

  import CategoryModels._

  def categoryGet = (path("category") & get & openid) { id =>
    complete(handleCategoryGet(id).toResult)
  }
}
