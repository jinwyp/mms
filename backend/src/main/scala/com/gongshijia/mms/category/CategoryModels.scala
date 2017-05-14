package com.gongshijia.mms.category

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/12.
  */
object CategoryModels extends DefaultJsonProtocol {

  // 预定义类目
  val defaultCategories: List[Category] = List(
    Category("造型"),
    Category("美甲"),
    Category("茶艺"),
    Category("插花"),
    Category("搭配"),
    Category("健身")
  )

  // 类目
  case class Category(name: String, checked: Boolean = false)
  implicit val CategoryResultFormat = jsonFormat2(Category)

  case class CategoriesRequest (categories:List[String])
  implicit val CategoriesRequestFormat = jsonFormat1(CategoriesRequest)

  case class CategoriesResponse(categories: List[Category])
  implicit val CategoriesResponseFormat = jsonFormat1(CategoriesResponse)
}
