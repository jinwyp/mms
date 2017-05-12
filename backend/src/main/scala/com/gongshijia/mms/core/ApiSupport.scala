package com.gongshijia.mms.core

import spray.json.DefaultJsonProtocol

import scala.concurrent.{ExecutionContext, Future}

/**
  * Created by hary on 2017/5/12.
  */
trait ApiSupport extends DefaultJsonProtocol {

  import spray.json.JsonFormat

  // API接口定义
  case class Pagination(totalItem: Int, totalPage: Int, pageSize: Int, curPage: Int)

  case class Error(code: Int, message: String)

  case class Result[T](data: Option[T], success: Boolean, error: Option[Error] = None)

  implicit val PaginationFormat = jsonFormat4(Pagination)
  implicit val ErrorFormat = jsonFormat2(Error)

  implicit def resultFormat[A: JsonFormat] = jsonFormat3(Result.apply[A])

  def failed(code: Int, message: String) = Result[Int](None, false, Some(Error(code, message)))

  def success[T](data: T): Result[T] = Result(Some(data), success = true)

  implicit class DomainFutureToResult[T](df: Future[T]) {
    def toResult(implicit ec: ExecutionContext): Future[Result[T]] = df.map(success _)
  }

}
