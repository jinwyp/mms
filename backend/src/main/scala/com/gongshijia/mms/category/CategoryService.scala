package com.gongshijia.mms.category


import com.gongshijia.mms.core.{Core, MongoSupport}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model._
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.Future

import  com.mongodb.client.model.Projections._



/**
  * Created by hary on 2017/5/12.
  */
trait CategoryService extends Core with MongoSupport {

  //添加关注的类目
  def addCategoriesToUser(openid: String, artsList: List[String]): Future[UpdateResult] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.updateOne(equal("openid", openid), Updates.addEachToSet("categories",artsList)).toFuture()
  }

  // 查找用户关注的类目
  def findCategoriesForUser(openId: String): Future[Option[List[String]]] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", openId)).projection(fields(include("categories"))).first().toFuture().map(_.categories)
  }

  def findCategoriesForUser2(openId: String): Future[User] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", openId)).first().toFuture()
  }
}
