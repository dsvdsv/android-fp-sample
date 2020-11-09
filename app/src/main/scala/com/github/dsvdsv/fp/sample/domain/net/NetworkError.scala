package com.github.dsvdsv.fp.sample.domain.net

sealed trait NetworkError extends Throwable with Product with Serializable

object NetworkError {
  case class DecodeError(msg:String) extends NetworkError
  case object NotFound extends NetworkError
  case object ServerError extends NetworkError
}