package com.gongshijia.mms

import com.gongshijia.mms.core.{MongoTestSupport, Neo4jSupport}
import com.gongshijia.mms.user.login.LoginService
import org.mongodb.scala.model.{Filters, UpdateOptions}
import org.mongodb.scala.{MongoClient, MongoCollection}
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.UpdateOptions
import org.mongodb.scala.result.UpdateResult

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Created by xiangyang on 2017/5/13.
  */
object CategoryServiceTest extends App with Neo4jSupport{
  val x = "oiBDr0M-MinCSj0pVPJG3Z6kcZZw"
  val id="591ad1043a4abea073a8b489"
// findUserByOpenId(x)
  createFriendRelationShip("a","b","1233")
//  collection.find(Document("_id"->id)).results()

}
