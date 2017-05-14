package com.gongshijia.mms.category


import com.gongshijia.mms.core.{Core, MongoSupport}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model._


import scala.concurrent.Future


/**
  * Created by hary on 2017/5/12.
  */
trait CategoryService extends Core with MongoSupport {

  //添加关注的类目
  def addCategoriesToUser(openid: String, artsList: List[String]): Future[Boolean] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.updateOne(equal("openid", openid),addEachToSet("categories", artsList)).toFuture().map(_ => true)
  }

  // 查找用户关注的类目
  def findCategoriesForUser(openid: String): Future[Option[List[String]]] = {
    val collection: MongoCollection[Map[String, List[String]]] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", openid)).projection(fields(include("categories"), excludeId())).first().toFuture().map(_.get("categories"))
  }

}
