package com.gongshijia.mms.test

import org.mongodb.scala.bson.ObjectId
import spray.json.DefaultJsonProtocol

/**
  * Created by hary on 2017/5/3.
  */
object Models extends DefaultJsonProtocol {


  // Create the case class
  object Person {
    def apply(firstName: String, lastName: String): Person = Person(new ObjectId(), firstName, lastName);
  }

  case class Person(_id: ObjectId, firstName: String, lastName: String)

//  implicit val PersonResponseFormat = jsonFormat3(Person)

}
