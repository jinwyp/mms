package com.gongshijia.mms.test


import java.util.Date

import com.gongshijia.mms.Core
import org.mongodb.scala.bson.ObjectId

/**
  * Created by hary on 2017/5/3.
  */
trait Models extends Core {

  case class Address(name: String, houseNum: Option[String])

  case class Course(cName: String, score: List[Double])

  case class Person(firstName: String,
                    lastName: String,
                    registerDate: Date,
                    fullName: Option[String],
                    address: List[Address],
                    courses: List[Course])

  implicit val AddressFormat = jsonFormat2(Address);
  implicit val CourseFormat = jsonFormat2(Course);
  implicit val PersonFormat = jsonFormat6(Person);
  implicit val PersonDTOFormat= jsonFormat7(PersonDTO);


  //数据库领域对象
//  object PersonDTO {
//
//    def apply(firstName: String, lastName: String, registerDate: Date, fullName: Option[String], address: List[Address], courses: List[Course]): PersonDTO =
//      PersonDTO(new ObjectId(), firstName, lastName, registerDate, fullName, address, courses);
//
//  }

  case class PersonDTO(_id: ObjectId,
                       firstName: String,
                       lastName: String,
                       registerDate: Date,
                       fullName: Option[String],
                       address: List[Address],
                       courses: List[Course])

}

object Models extends Models
