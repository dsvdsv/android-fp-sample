package com.github.dsvdsv.fp.sample.domain.net

sealed trait ExchangeError extends Throwable with Product with Serializable

object ExchangeError {
  case class DecodeError(msg: String)        extends RuntimeException(msg) with ExchangeError
  case class NotFound(msg: String)           extends RuntimeException(msg) with ExchangeError
  case class CommunicationError(msg: String) extends RuntimeException(msg) with ExchangeError
  case class ServerError(msg: String)        extends RuntimeException(msg) with ExchangeError

  def decodeError(msg: String): ExchangeError        = DecodeError(msg)
  def notFound(msg: String): ExchangeError           = NotFound(msg)
  def communicationError(msg: String): ExchangeError = CommunicationError(msg)
  def serverError(msg: String): ExchangeError        = ServerError(msg)
}
