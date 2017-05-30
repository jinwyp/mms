package com.gongshijia.mms

import com.gongshijia.mms.core.{MongoSupport, MongoTestSupport, Neo4jSupport}
import com.gongshijia.mms.experience.ExperienceReportController
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

import scala.async.Async.{async, await}
import scala.concurrent.{Await, Future}
import scala.concurrent.Await._
import scala.concurrent.duration._

/**
  * Created by xiangyang on 2017/5/13.
  */
object ExperienceReportServiceTest extends App with MongoSupport with MongoTestSupport with Neo4jSupport with ExperienceReportController {

  val reportId = "592d6b233392242425c96d2e"
  val openId = "o8ODs0PNKm_AANqmvF0wKOvIjiYs"

  //    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection("exp_report")

  //  collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openId))).first().printResults()

  //  val friendOpenIds: Seq[String] = loadMyFriend("oiBDr0M-MinCSj0pVPJG3Z6kcZZw")
  //  println("--------------" + friendOpenIds)
  //  println(collection.find(Filters.in("openid", friendOpenIds: _*)).limit(10).skip(0).toFuture())
  //  collection.find(Filters.in("openid", friendOpenIds: _*)).limit(10).skip(0).printResults()


  val collection: MongoCollection[CollectInfo] = mongoDb.getCollection("collectInfo")
  //  val a =Document(s"""{$pull:{reportList:{reportId:$reportId}}}""")
  //  collection.updateOne(equal("openid", openId), pullByFilter(equal("reportList",equal("reportId",reportId)))).results()
  //   println(await(collection.count(and(equal("openid", openId),equal("reportList.reportId",reportId))).toFuture())))
    println(Await.result(findById(reportId, openId),3.seconds))
}

