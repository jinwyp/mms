package com.gongshijia.mms.experience

import java.util.Date

import com.gongshijia.mms.core.{Core, MongoSupport, Neo4jSupport}
import com.gongshijia.mms.experience.ExperienceReportModels.{CommentsRequest, ExperienceReportRequest, SignInfoRequest}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportController extends Core with MongoSupport with Neo4jSupport {

  private val collectionName = "exp_report"


  def addReport(openid: String, reportRequest: ExperienceReportRequest): Future[Boolean] = {
    val report = ExperienceReport(new ObjectId(), reportRequest.lon,
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
      openid, List(), List())
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.insertOne(report).toFuture().map { _ => true }
  }

  def listReport(openid: String, pageSize: Int, pageNum: Int, category: String): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.find(equal("openid", openid)).limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()
  }

  def findFriendReport(openid: String, pageSize: Int, pageNum: Int): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    val friendOpenIds=loadMyFriend(openid)
    collection.find(in("openid", friendOpenIds)).limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()
  }

  def addSignReport(openid: String, signInfo: SignInfoRequest): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    val flows = signInfo.flows.map(f => ArtFlow(f.flow, f.duration))
    val material = signInfo.material.map(m => Material(m.name, m.count))
    val sign = SignInfo(new ObjectId(), openid, 0, signInfo.realPicture, material, flows,signInfo.introd)
    collection.updateOne(equal("_id", new ObjectId(signInfo.reportid)), addEachToSet("signInfo", sign)).toFuture().map(_ => true)
  }

  def addComments(openid: String, commentsRequest: CommentsRequest): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    val comments = Comments(new ObjectId(), openid, new Date(), commentsRequest.content)
    collection.updateOne(equal("_id", new ObjectId(commentsRequest.reportid)), addEachToSet("comments", comments)).toFuture().map(_ => true)
  }

  def findById(id: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    collection.find(equal("_id", new ObjectId(id))).first().toFuture()
  }

  def findByIdAndOpenid(fromOpenId:String,currentOpenId: String,expId:String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(collectionName)
    for{
     created <- createFriendRelationShip(fromOpenId=fromOpenId,toOpenId = currentOpenId,expId=expId)
     report <- collection.find(and(equal("_id", new ObjectId(expId)), equal("openid", openid))).first().toFuture()
    }yield report
  }
}
