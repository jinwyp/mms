package com.gongshijia.mms.experience

import java.util.Date

import spray.json.DefaultJsonProtocol

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportModels extends DefaultJsonProtocol {

  /*

  */
  /**
    *
    * 耗材
    *
    * @param name  耗材名称
    * @param count 填写序号
    */
  case class Material(name: String, count: Int)

  implicit val MaterialFormat = jsonFormat2(Material)


  /**
    * 工艺流程
    *
    * @param flow     流程描述
    * @param duration 持续时间(分钟数)
    */
  case class ArtFlow(flow: String, duration: Int)

  implicit val ArtFlowFormat = jsonFormat2(ArtFlow)


  /**
    *  体验报告
    * @param lon          体验位置纬度
    * @param lat          体验位置经度
    * @param locationName 体验位置名称
    * @param expTime      体验时间
    * @param expPrice     体验价格
    * @param pictures     图片
    * @param feeling      个人感受
    *
    * @param material     耗材
    * @param flows        工艺流程
    * @param comments      评论
    *
    */
  case class ExperienceReport(lon: Double,
                              lat: Double,
                              locationName: String,
                              expTime: Date,
                              expPrice: Double,
                              pictures: List[String],
                              videos:Option[String],
                              category:String,
                              userOpenId:String,
                              signerOpenId:Option[List[String]],

                              feeling: String,


                              material: Option[List[Material]],
                              flows: Option[List[ArtFlow]],
                              comments: List[String]);
//  implicit val ExperienceFormat = jsonFormat10(ExperienceReport)


}
