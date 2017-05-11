package com.gongshijia.mms.test

import scala.concurrent.{Await, Future}

/**
  * Created by hary on 2017/5/10.
  */
object Async extends App {

  import scala.async.Async.{async, await}

  implicit val ec = scala.concurrent.ExecutionContext.global

  import scalaz._
  import Scalaz._
  import scala.concurrent.duration._

  case class Student(name: String, age: Int, score: Int)

  def getName = Future {
    "hary"
  }

  def getAge = Future {
    33
  }

  def getScore = Future {
    99
  }

  //  val getName = Future { "hary"}
  //  val getAge = Future {  33 }
  //  val getScore = Future {  99 }

//  // for-comprehension
  val fc: Future[Student] = for {
    n <- getName
    a <- getAge
    s <- getScore
  } yield Student(n, a, s)
  val tfc= Await.ready(fc, 3.seconds)
  println(s"the result is: $tfc")

//  // scalaz + sequence + traverse
  val fstudent = (getName |@| getAge |@| getScore) {
    Student(_, _, _)
  }

//  val st = Await.ready(fstudent, 3.seconds)
//  println(s"the result is: $st")
//
//  val fl: Future[List[Int]] = Future.traverse(List(1, 2, 3))(i => Future { i * i})
//  val lf = List( Future { 1 }, Future { 2 })
//  val fl2: Future[List[Int]] = Future.sequence(lf)
//
//  // async
  val fa: Future[Student] = async {
    val name: String = await(getName)
    if (name == "hary") {
      val age = await(getAge)
      val score = await(getScore)
      Student(name, age, score)
    } else {
      Student("k", 1, 1)
    }
  }

  // traverse && sequence

  //
  //
  //
  //  1> map flatMap filter   <=>   for comprehension
  //  2> scalaz:    { f1 |@| f2 |@| f3 } {  Student(_, _, _)
  //
  //

//  val f: Future[Int] = async {
//    if (await(Future { true })) {
//      await(Future { 2 })
//    } else {
//      0
//    }
//  }
//
//  val t = Await.ready(f, 3.seconds)
//  println(s"the result is: $t")
}
