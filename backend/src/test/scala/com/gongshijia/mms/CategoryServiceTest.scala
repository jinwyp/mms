package com.gongshijia.mms

import com.gongshijia.mms.core.{MongoSupport, MongoTestSupport}
import com.gongshijia.mms.experience.ExperienceReportController
import org.mongodb.scala.bson.ObjectId

/**
  * Created by xiangyang on 2017/5/13.
  */
object CategoryServiceTest extends App with MongoTestSupport with MongoSupport with ExperienceReportController {
  val oid = "oiBDr0M-MinCSj0pVPJG3Z6kcZZw"
  val reportId:String = "591d1b703a4abedc2844e746"
  val signId = "591d7815289bbf20eb742546"
//  updateSureSignInfo(oid, reportId,signId)
  //  val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection("exp_report")
  //  collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", x))).first().printResults()

}
