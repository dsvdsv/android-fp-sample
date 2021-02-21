package com.github.dsvdsv.fp.sample.runtime

import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Resource}
import com.github.dsvdsv.fp.sample.domain.UIApi
import com.github.dsvdsv.fp.sample.domain.config.Config
import org.http4s.client.Client
import org.http4s.client.okhttp.OkHttpBuilder

object Runtime {
  val defaultConfig = {
    import org.http4s.implicits.http4sLiteralsSyntax

    Config(uri"https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
  }

  def uiApi[F[_]: ConcurrentEffect: ContextShift]: Resource[F, UIApi[F]] =
    for {
      httpClient <- httpClient[F]
      uiApi      <- Resource.liftF(UIApi.make(defaultConfig, httpClient))
    } yield uiApi

  def httpClient[F[_]: ConcurrentEffect: ContextShift]: Resource[F, Client[F]] =
    for {
      blocker       <- Blocker[F]
      okHttpBuilder <- OkHttpBuilder.withDefaultClient[F](blocker)
      client        <- okHttpBuilder.resource
    } yield client
}