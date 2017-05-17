package com.gongshijia.mms.category

import com.gongshijia.mms.core.{Core, MongoSupport}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.pushEach
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.Future

/**
  * Created by hary on 2017/5/12.
  */
trait CategoryService extends Core with MongoSupport {

  //添加关注的类目
  def addCategoriesToUser(openid: String, artsList: List[String]): Future[UpdateResult] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.updateOne(equal("openid", openid), pushEach("arts", artsList)).toFuture()
  }

  //
  def findCategoriesForUser(openId: String): Future[Option[List[String]]] = {
    val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
    collection.find(equal("openid", openId)).first().toFuture().map(_.categories)
  }
}
