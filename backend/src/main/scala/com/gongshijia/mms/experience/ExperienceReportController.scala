package com.gongshijia.mms.experience

import java.util.Date

import com.gongshijia.mms.core.{Core, MongoSupport, Neo4jSupport}
import com.gongshijia.mms.experience.ExperienceReportModels.{CommentsRequest, ExperienceReportRequest, SignInfoRequest}
import com.gongshijia.mms.user.login.LoginService
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._

import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportController extends Core with MongoSupport with Neo4jSupport with LoginService {

  private val reportCollection = "exp_report"
  private val collectCollection = "collectInfo"


  def addReport(openid: String, reportRequest: ExperienceReportRequest): Future[Boolean] = {
    findUserByOpenId(openid) map { user =>
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
        openid,  user.avatarUrl,user.nickName)
      val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
      collection.insertOne(report).toFuture().isCompleted
    }
  }

  def listReport(openid: String, pageSize: Int, pageNum: Int, category: String): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    collection.find().limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()
  }

  def findFriendReport(openid: String, pageSize: Int, pageNum: Int): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    val friendOpenIds = loadMyFriend(openid)
    collection.find(in("openid", friendOpenIds)).limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()
  }

  def addSignReport(openid: String, signInfo: SignInfoRequest): Future[Boolean] = {
    findUserByOpenId(openid) map { user =>
      val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
      val flows = signInfo.flows.map(f => ArtFlow(f.flow, f.duration))
      val material = signInfo.material.map(m => Material(m.name, m.count))
      val sign = SignInfo(new ObjectId(), openid, user.avatarUrl,new Date(),0,signInfo.realPicture, material, flows, signInfo.introd)
      collection.updateOne(equal("_id", new ObjectId(signInfo.reportid)), addEachToSet("signInfo", sign)).toFuture().isCompleted
    }
  }

  def addComments(openid: String, commentsRequest: CommentsRequest): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    val comments = Comments(new ObjectId(), openid, new Date(), commentsRequest.content)
    collection.updateOne(equal("_id", new ObjectId(commentsRequest.reportid)), addEachToSet("comments", comments)).toFuture().map(_ => true)
  }

  //添加到收藏
  def addReportToCollect(openid: String, reportid: String): Future[Boolean] = {
    val collection: MongoCollection[CollectInfo] = mongoDb.getCollection(collectCollection)
   val updateOptions= UpdateOptions().upsert(true)
    collection.updateOne(equal("openid", openid), addEachToSet("reportids", reportid),updateOptions).toFuture().map(_ => true)
  }

  //移除收藏的报告
  def removeCollectReport(openid: String, reportid: String): Future[Boolean] = {
    val collection: MongoCollection[CollectInfo] = mongoDb.getCollection(collectCollection)
    collection.updateOne(equal("openid", openid), pull("reportids", reportid)).toFuture().map(_ => true)
  }

  def findById(id: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    collection.find(equal("_id", new ObjectId(id))).first().toFuture()
  }

  def findByIdAndOpenid(fromOpenId: String, currentOpenId: String, reportId: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    for {
      created <- createFriendRelationShip(fromOpenId = fromOpenId, toOpenId = currentOpenId, expId = reportId)
      report <- collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openid))).first().toFuture()
    } yield report
  }

  def updateSureSignInfo(openId:String,reportId: String,  signInfoId: String): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
     collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openid))).first().toFuture() map{ report=>
         collection.updateOne(and(equal("_id", new ObjectId(reportId)), equal("signInfo._id", signInfoId)),set("signInfo.$.checked",1)).toFuture().isCompleted
     }
  }
}
