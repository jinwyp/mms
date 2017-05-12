package com.gongshijia.mms.misc

import spray.json.DefaultJsonProtocol

// 领域信息
trait Domain extends DefaultJsonProtocol {

  // 耗材   name  耗材名称   count 填写序号
  case class Material(name: String, count: Int)
  implicit val MaterialFormat = jsonFormat2(Material)

  // 工艺流程    flow 流程描述      duration 持续时间(分钟数)
  case class ArtFlow(flow: String, duration: Int)
  implicit val ArtFlowFormat = jsonFormat2(ArtFlow)


  //体验报告
  case class Experience(lat: Double, lon: Double, name: String, exptime: String, expprice: Int, pictures: List[String], materials: Option[List[Material]], flows: Option[List[ArtFlow]], feeling: String, comments: List[String]);
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
  case class ExpSignRequest(expid: String)
  case class ExpSignResponse(expid: String)
  implicit val ExpSignRequestFormat = jsonFormat1(ExpSignRequest)
  implicit val ExpSignResponseFormat = jsonFormat1(ExpSignResponse)

  // 类目
  case class CategoriesRequest(categories: List[String])
  case class CategoriesResponse(redirect: Int, favoriteResp: Option[Map[String, List[Experience]]])
  implicit val CategoriesRequestFormat = jsonFormat1(CategoriesRequest)
  implicit val CategoriesResponseFormat = jsonFormat2(CategoriesResponse)

  // 评价
  case class CommentRequest(content: String, reportId: String)
  case class CommentResponse()
  implicit val CommentFormat = jsonFormat2(CommentRequest)

  // 收藏
  type FavoriteRequest = Favorite


}
