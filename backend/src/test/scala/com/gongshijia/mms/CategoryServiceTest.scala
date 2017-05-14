package com.gongshijia.mms

import com.gongshijia.mms.category.CategoryService
import com.gongshijia.mms.core.{MongoSupport, MongoTestSupport}

import scala.concurrent.{Future,Await}
/**
  * Created by xiangyang on 2017/5/13.
  */
object CategoryServiceTest extends App with CategoryService with MongoTestSupport with MongoSupport {

  val x = "oiBDr0M-MinCSj0pVPJG3Z6kcZZw"
  val future1: Future[Option[List[String]]] = findCategoriesForUser(x)
  val result = Await.ready(future1, 3.seconds);
  println(result);

  //  val future2: Future[User] = findCategoriesForUser2(x)
  //  val result2 = Await.ready(future2,3.seconds);
  //  println(future2);
  //  val collection: MongoCollection[User] = mongoDb.getCollection("userinfo")
  // val a: Future[_root_.com.gongshijia.mms.CategoryServiceTest.User] = collection.find().projection(fields(exclude("avatarUrl"))).first().toFuture()
  //  collection.find().printResults();

  //    val collection: MongoCollection[OSSAsset] = mongoDb.getCollection("uploadFile_record");
  //      collection.find().printResults()
  println("sfsdf");

}
