package com.gongshijia.mms.test

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives.{complete, get, path, post, _}
import com.gongshijia.mms.Core
import com.gongshijia.mms.test.Models.Person
import org.mongodb.scala._
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.Updates.set
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._
/**
  * Created by hary on 2017/5/3.
  */
trait MongoRoute extends Core with SprayJsonSupport {


  val codecRegistry = fromRegistries(fromProviders(classOf[Person]), DEFAULT_CODEC_REGISTRY)

  // mongo example
  def mongoInsert = path("insert") {
    post {
      val mongoClient: MongoClient = MongoClient();
      val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
      val collection: MongoCollection[Person] = database.getCollection("userinfo")
      collection.drop().results();

      val person: Person = Person("A", "A")
      collection.insertOne(person).results()


      val people: Seq[Person] = Seq(
        Person("B", "B"),
        Person("C", "C")
      )
      collection.insertMany(people).printResults()
      complete("ok")

    }
  }

  def mongoUpsert = path("upsert") {
    post {
      val mongoClient: MongoClient = MongoClient();
      val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
      val collection: MongoCollection[Person] = database.getCollection("userinfo")
      collection.updateOne(equal("firstName", "A"), set("firstName", "AA")).printHeadResult("Update Result: ")
      mongoClient.close();
      complete("ok")
    }
  }

  def mongoDelete = path("delete") {
    post {
      complete("ok")
    }
  }

  def mongoPager = path("pager") {
    get {
      val mongoClient: MongoClient = MongoClient();
      val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
      val collection: MongoCollection[Person] = database.getCollection("userinfo")

//      collection.find().limit(10).skip();
      mongoClient.close();
      complete("ok")
    }
  }

  def mongoSelect = path("select") {
    get {
      complete("ok")
    }
  }

  def mongoRoute = mongoInsert ~ mongoUpsert ~ mongoDelete ~ mongoPager ~ mongoSelect
}
