package com.github.dsvdsv.fp.sample.domain.net

sealed trait NetworkError extends Throwable with Product with Serializable

object NetworkError {
  case object NotFound extends NetworkError
  case object ServerError extends NetworkError
}