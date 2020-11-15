package com.github.dsvdsv.fp.sample.domain.net

sealed trait NetworkError extends Throwable with Product with Serializable

object NetworkError {
  case class DecodeError(msg: String)        extends RuntimeException(msg) with NetworkError
  case class NotFound(msg: String)           extends RuntimeException(msg) with NetworkError
  case class CommunicationError(msg: String) extends RuntimeException(msg) with NetworkError
  case class ServerError(msg: String)        extends RuntimeException(msg) with NetworkError
}
