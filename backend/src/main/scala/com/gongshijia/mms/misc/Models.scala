package com.gongshijia.mms.misc

import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/5.
  */
trait Models extends DefaultJsonProtocol {

  // 体验报告
  case class Material(name: String, count: Int)
  case class ArtFlow(flow: String, duration: Int)
  case class Experience(location_lat: Double, location_lon: Double, location_name: String, exp_time: String, exp_price: Int, pictures: List[String], materials: List[Material], flows: List[ArtFlow], feeling: String, comments: List[String] );
  implicit val MaterialFormat = jsonFormat2(Material)
  implicit val ArtFlowFormat = jsonFormat2(ArtFlow)
  implicit val ExperienceFormat = jsonFormat10(Experience)

  // 获取类目
  case class CategoryResult(name: String, checked: Boolean = false)
  case class CategoriesRequest(categories: List[String])
  case class CategoriesResponse(redirect: Int, experiences: Map[String, List[Experience]])
  implicit val CategoryResultFormat = jsonFormat2(CategoryResult)
  implicit val CategoriesRequestFormat = jsonFormat1(CategoriesRequest)
  implicit val CategoriesResponseFormat = jsonFormat2(CategoriesResponse)

  // 预定义类目
  val artsList: Seq[CategoryResult] = List(
    CategoryResult("造型"),
    CategoryResult("美甲"),
    CategoryResult("茶艺"),
    CategoryResult("插花"),
    CategoryResult("搭配"),
    CategoryResult("健身")
  )

  // 评价
  case class CommentRequest(content: String, reportId: String)
  implicit val CommentFormat = jsonFormat2(CommentRequest)
}
