package com.gongshijia.mms

import com.gongshijia.mms.core.{MongoSupport, MongoTestSupport, Neo4jSupport}
import org.bson.types.ObjectId
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Filters.{and, equal, in}

/**
  * Created by xiangyang on 2017/5/13.
  */
object ExperienceReportServiceTest extends App with MongoSupport with MongoTestSupport with Neo4jSupport {

  val reportId = "5926d8ed339224584974e529"
  val openId = "oiBDr0OSwoMxJOHy1jI2oSIPmVOM"

  val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection("exp_report")

  //  collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openId))).first().printResults()

  val friendOpenIds: Seq[String] = loadMyFriend("oiBDr0M-MinCSj0pVPJG3Z6kcZZw")
  println("--------------" + friendOpenIds)
//  println(collection.find(Filters.in("openid", friendOpenIds: _*)).limit(10).skip(0).toFuture())
  collection.find(Filters.in("openid", friendOpenIds: _*)).limit(10).skip(0).printResults()

}
