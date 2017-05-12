package com.gongshijia.mms.core

/**
  * Created by hary on 2017/5/12.
  */
// 异常定义
trait ExceptionSupport {

  sealed trait InternalException extends Exception {
    val message: String
  }

  case class DatabaseException(message: String) extends InternalException

  case class ParameterException(message: String) extends InternalException

  case class BusinessException(message: String) extends InternalException

  case class UnauthorizedException(message: String) extends InternalException

}
