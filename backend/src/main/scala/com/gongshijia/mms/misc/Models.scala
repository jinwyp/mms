package com.gongshijia.mms.misc

import spray.json.DefaultJsonProtocol

// 领域信息
trait Domain extends DefaultJsonProtocol {
  // 体验报告
  case class Material(name: String, count: Int)
  case class ArtFlow(flow: String, duration: Int)
  case class Experience(lat: Double, lon: Double, name: String, exptime: String, expprice: Int, pictures: List[String], materials: List[Material], flows: List[ArtFlow], feeling: String, comments: List[String] );
  implicit val MaterialFormat = jsonFormat2(Material)
  implicit val ArtFlowFormat = jsonFormat2(ArtFlow)
  implicit val ExperienceFormat = jsonFormat10(Experience)

  // 类目
  case class Category(name: String, checked: Boolean = false)
  implicit val CategoryResultFormat = jsonFormat2(Category)

  // 收藏
  case class Favorite(openid: String, expid: String)
  implicit val FavoriteFormat = jsonFormat2(Favorite)

  // 分享
  case class Sharing(from: String, to: String, expid: String)
  implicit val SharingFormat = jsonFormat3(Sharing)

  // 预定义类目
  val artsList: Seq[Category] = List(
    Category("造型"),
    Category("美甲"),
    Category("茶艺"),
    Category("插花"),
    Category("搭配"),
    Category("健身")
  )
}

/**
  * Created by hary on 2017/5/5.
  */
trait Models extends DefaultJsonProtocol with Domain {

  // 署名
  case class ExpSignRequest(exp_id: String)
  case class ExpSignResponse(exp_id: String)

  // 类目
  case class CategoriesRequest(categories: List[String])
  case class CategoriesResponse(redirect: Int, experiences: Map[String, List[Experience]])
  implicit val CategoriesRequestFormat = jsonFormat1(CategoriesRequest)
  implicit val CategoriesResponseFormat = jsonFormat2(CategoriesResponse)

  // 评价
  case class CommentRequest(content: String, reportId: String)
  case class CommentResponse()
  implicit val CommentFormat = jsonFormat2(CommentRequest)
}
