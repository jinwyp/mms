package com.gongshijia.mms.experience

import java.util.Date

import com.gongshijia.mms.core.{HttpSupport, MongoSupport}
import org.mongodb.scala.bson.ObjectId
import spray.json.DefaultJsonProtocol

/**
  * Created by xiangyang on 2017/5/13.
  */
object ExperienceReportModels extends DefaultJsonProtocol with HttpSupport {


  /**
    * 耗材
    *
    * @param name  耗材名称
    * @param count 填写序号
    */
  case class MaterialRequest(name: String, count: Int)


  implicit val MaterialFormat = jsonFormat2(MaterialRequest)


  /**
    * 工艺流程
    *
    * @param flow     流程描述
    * @param duration 持续时间(分钟数)
    */
  case class ArtFlowRequest(flow: String, duration: Int)

  implicit val ArtFlowFormat = jsonFormat2(ArtFlowRequest)


  /**
    * 评论内容
    *
    * @param openid
    * @param content
    */
  case class CommentsRequest(reportid: String, openid: String, content: String)

  implicit val CommentsFormat = jsonFormat3(CommentsRequest)

  /**
    *
    * 署名信息
    *
    * @param checked     用户是否确认
    * @param realPicture 图片
    * @param material    耗材
    * @param flows       工艺流程
    */
  case class SignInfoRequest(reportid: String, checked: Boolean = false, realPicture: List[String], material: List[MaterialRequest], flows: List[ArtFlowRequest], introd: String)

  implicit val SignInfoRequestFormat = jsonFormat6(SignInfoRequest)

  /**
    *
    * @param lon          体验位置纬度
    * @param lat          体验位置经度
    * @param locationName 体验位置名称
    * @param shopName     体验时间
    * @param expTime      体验价格
    * @param expPrice     体验价格
    * @param pictures     图片
    * @param videos       视频
    * @param feeling      个人感受
    * @param category     类别
    * @param privacy      权限:  私密， 好友可见， 公开
    * @param pricePrivacy 价格权限
    * @param signInfo
    * @param comments
    */
  case class ExperienceReportRequest(lon: Double,
                                     lat: Double,
                                     locationName: String,
                                     shopName: String,
                                     expTime: Date,
                                     expPrice: Double,
                                     pictures: List[String],
                                     videos: Option[String],
                                     feeling: String,
                                     category: String,
                                     privacy: Int,
                                     pricePrivacy: Int,
                                     signInfo: Option[List[SignInfoRequest]],
                                     comments: Option[List[CommentsRequest]]);

  implicit val ExperienceReportRequestFormat = jsonFormat14(ExperienceReportRequest)



}
