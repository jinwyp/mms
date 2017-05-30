package com.gongshijia.mms.experience

import java.util.Date

import com.gongshijia.mms.core._
import com.gongshijia.mms.experience.ExperienceReportModels.{CommentsRequest, ExperienceReportRequest, SignInfoRequest}
import com.gongshijia.mms.user.login.LoginService
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.model.Updates._

import scala.async.Async.{async, await}
import scala.concurrent.Future

/**
  * Created by xiangyang on 2017/5/13.
  */
trait ExperienceReportController extends Core with MongoSupport with Neo4jSupport with LoginService with AppConfig {

  private val reportCollection = "exp_report"
  private val collectCollection = "collectInfo"


  def addReport(openid: String, reportRequest: ExperienceReportRequest): Future[Boolean] = {
    findUserByOpenId(openid) flatMap { user =>
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
        openid, user.avatarUrl, user.nickName)
      val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
      collection.insertOne(report).toFuture().map(_ => true)
    }
  }

  //加载好友体验报告
  def findFriendReport(openid: String, pageSize: Int, pageNum: Int): Future[Seq[ExperienceReport]] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    val friendOpenIds = loadMyFriend(openid)
    async {
      await(collection.find(in("openid", friendOpenIds: _*)).limit(pageSize).skip((pageNum - 1) * pageSize).toFuture()).map { t =>
        t.copy(pictures = t.pictures.map(localossHost.concat(_)))
      }
    }
  }


  //加载收藏
  def findCollectReport(openid: String, category: String): Future[Seq[ExperienceReport]] = {
    val collectInfo: MongoCollection[CollectReport] = mongoDb.getCollection(collectCollection)
    val reportCol: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    async {
      await(reportCol.find().toFuture()).map { t =>
        t.copy(pictures = t.pictures.map(localossHost.concat(_)))
      }
    }
  }

  //添加体验报告署名
  def addSignReport(openid: String, signInfo: SignInfoRequest): Future[Boolean] = {
    findUserByOpenId(openid) map { user =>
      val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
      val flows = signInfo.flows.map(f => ArtFlow(f.flow, f.duration))
      val material = signInfo.material.map(m => Material(m.name, m.count))
      val sign = SignInfo(new ObjectId(), openid, user.avatarUrl, user.userName.get, user.shopName.get, user.workAddress.get, new Date(), 0, signInfo.realPicture, material, flows, signInfo.introd)
      collection.updateOne(equal("_id", new ObjectId(signInfo.reportid)), addEachToSet("signInfo", sign)).toFuture().isCompleted
    }
  }

  // 体验报告确认署名信息
  def makeSureSignInfo(openId: String, reportId: String, signInfoId: String): Future[Boolean] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openId))).first().toFuture() flatMap { report =>
      collection.updateOne(and(equal("_id", new ObjectId(reportId)), equal("signInfo._id", new ObjectId(signInfoId))), set("signInfo.$.checked", 1)).toFuture().isCompleted
      collection.updateOne(equal("_id", new ObjectId(reportId)), set("checked", 1)).toFuture().map(t => true)
    }
  }


  def findById(id: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    for {
      r1 <- collection.find(equal("_id", new ObjectId(id))).first().toFuture().map { t => t.copy(pictures = t.pictures.map(localossHost.concat(_))) }
    //      r2 <- r1
    //      val signList = r1.signInfo.filter(_.checked == true)
    //      val a = r1.copy(signInfo = signList.map(s => s.copy(realPicture = s.realPicture.map(localossHost.concat((_))))))
    } yield r1
  }

  def findByIdAndOpenId(fromOpenId: String, currentOpenId: String, reportId: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    async {
      await(createFriendRelationShip(fromOpenId = fromOpenId, toOpenId = currentOpenId, expId = reportId))
      replacePic(await(collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", fromOpenId))).first().toFuture()))
    }
  }


  def findByIdAndOpenId(openId: String, reportId: String): Future[ExperienceReport] = {
    val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
    collection.find(and(equal("_id", new ObjectId(reportId)), equal("openid", openId))).first().toFuture()
  }

  //添加体验报告评论
  def addComments(openid: String, commentsRequest: CommentsRequest): Future[Comments] = {
    findUserByOpenId(openid) map { user =>
      val collection: MongoCollection[ExperienceReport] = mongoDb.getCollection(reportCollection)
      val comments = Comments(new ObjectId(), openid, user.nickName, user.avatarUrl, new Date(), commentsRequest.content)
      collection.updateOne(equal("_id", new ObjectId(commentsRequest.reportid)), addEachToSet("comments", comments)).toFuture.isCompleted
      comments
    }
  }

  //添加到收藏
  def addReportToCollect(openid: String, reportid: String): Future[Boolean] = {
    val collection: MongoCollection[CollectInfo] = mongoDb.getCollection(collectCollection)
    val updateOptions = UpdateOptions().upsert(true)
    collection.find(equal("openid", openid)).toFuture() flatMap { c =>
      if (c == null) {
        val co = CollectInfo(new ObjectId(), openid, List(CollectReport(reportid, "茶艺")))
        collection.insertOne(co).toFuture().map(_ => true)
      } else {
        collection.updateOne(equal("openid", openid), addEachToSet("reportList", CollectReport(reportid, "茶艺")), updateOptions).toFuture().map(_ => true)
      }
    }
  }

  //移除收藏的报告
  def removeCollectReport(openid: String, reportid: String): Future[Boolean] = {
    val collection: MongoCollection[CollectInfo] = mongoDb.getCollection(collectCollection)
    collection.updateOne(equal("openid", openid), pullByFilter(equal("reportList.reportId", reportid))).toFuture.map(_ => true)
  }



  def replacePic(experienceReport: ExperienceReport): ExperienceReport = {
    //替换报告图片
    val a = experienceReport.copy(pictures = experienceReport.pictures.map(localossHost.concat(_)))
    //替换署名图片
    val sign = a.signInfo.map(t => t.copy(realPicture = t.realPicture.map(localossHost.concat(_))))
    a.copy(signInfo = sign)
  }

}
