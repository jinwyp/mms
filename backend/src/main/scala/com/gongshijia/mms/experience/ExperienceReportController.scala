package com.gongshijia.mms.experience

import java.util.Date

import com.gongshijia.mms.core.{Core, MongoSupport}
import com.gongshijia.mms.experience.ExperienceReportModels.ExperienceReportRequest
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Filters._

import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportController extends Core with MongoSupport {

  private val collectionName= "exp_report"



  def addReport(openid: String, reportRequest: ExperienceReportRequest): Future[Boolean] = {
    val report = ExperienceReport(reportRequest.lon,
      reportRequest.lat,
      reportRequest.locationName,
      reportRequest.shopName,
      new Date(),
      reportRequest.expPrice,
      reportRequest.pictures,
      reportRequest.videos,
      reportRequest.feeling,
      reportRequest.category,
      reportRequest.privacy,
      reportRequest.pricePrivacy,
      openid)
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.insertOne(report).toFuture().map { _ => true }
  }

  def listReport(openid:String,pageSize: Int, pageNum: Int, category: String): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.find(and(equal("category", category),equal("openid",openid))).limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()
  }

  def addSignReport(id: String, openid: String, signInfo: SignInfo): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    signInfo.copy(openid=openid)
    collection.updateOne(equal("_id", id), addEachToSet("signInfo", signInfo)).toFuture().map(_ => true)
  }

  def addComments(id: String, openid: String, comments: Comments): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    comments.copy(openid=openid)
    collection.updateOne(equal("_id", id), addEachToSet("comments", comments)).toFuture().map(_ => true)
  }

  def findById(id: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.find(equal("_id", id)).first().toFuture()
  }
}
