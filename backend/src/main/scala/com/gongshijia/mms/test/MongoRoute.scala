package com.gongshijia.mms.test


import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Directives._
import com.gongshijia.mms.Core
import org.mongodb.scala._
import org.mongodb.scala.bson.ObjectId
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Updates._

// Create a codec for the Person case class
import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
import org.mongodb.scala.bson.codecs.Macros._

/**
  * Created by hary on 2017/5/3.
  */
trait MongoRoute extends Models with Core with SprayJsonSupport {


  val codecRegistry = fromRegistries(fromProviders(classOf[PersonDTO], classOf[Address], classOf[Course]), DEFAULT_CODEC_REGISTRY);


  // mongo example
  def mongoInsert = path("insert") {
    val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
    val collection: MongoCollection[PersonDTO] = database.getCollection("userinfo")
    post {
      entity(as[Person]) { person =>
        val dto = PersonDTO(new ObjectId(), person.firstName, person.lastName, person.registerDate, person.fullName, person.address, person.courses);

        onSuccess(collection.insertOne(dto).toFuture().map(_ => true)) {
          case true => complete(person);
        }
      }
    }
  }

  def mongoSelect = path("select") {
    get {
      val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
      val collection: MongoCollection[PersonDTO] = database.getCollection("userinfo")
      onSuccess(collection.find().toFuture()) {
        persons => {
          complete(persons)
        };
      }
    }
  }

  def mongoUpsert = path("upsert") {
    post {
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
      val database: MongoDatabase = mongoClient.getDatabase("test").withCodecRegistry(codecRegistry)
      val collection: MongoCollection[Person] = database.getCollection("userinfo")
      onSuccess(collection.find().limit(5).skip(2).toFuture()) {
        persons => complete(persons);
      }
    }
  }


  def mongoRoute = mongoInsert ~ mongoUpsert ~ mongoDelete ~ mongoPager ~ mongoSelect
}
