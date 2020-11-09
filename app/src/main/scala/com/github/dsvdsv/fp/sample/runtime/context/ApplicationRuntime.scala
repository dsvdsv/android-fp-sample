package com.github.dsvdsv.fp.sample.runtime.context

import cats.tagless.implicits._
import cats.Functor
import cats.arrow.FunctionK
import cats.data.EitherT
import cats.effect.{Blocker, ConcurrentEffect, ContextShift, Resource, Sync}
import cats.mtl.Raise
import com.github.dsvdsv.fp.sample.domain.net.{ExchangeLoader, NetworkError, RateList}
import com.github.dsvdsv.fp.sample.runtime.util.ExecutionContexts
import org.http4s.client.okhttp.OkHttpBuilder
import org.http4s.implicits.http4sLiteralsSyntax

trait ApplicationRuntime[F[_]] {
  def fetchRates(): EitherT[F, NetworkError, RateList]
}

object ApplicationRuntime {
  def launch[F[_]: ConcurrentEffect: ContextShift]: Resource[F, ApplicationRuntime[F]] =
    for {
      networkEx     <- ExecutionContexts.fixedThreadPool(2)
      okHttpBuilder <- OkHttpBuilder.withDefaultClient[F](Blocker.liftExecutionContext(networkEx))
      client        <- okHttpBuilder.resource
      loader        <- Resource.liftF(ExchangeLoader.fromClient[F](client))
    } yield new ApplicationRuntime[F] {
      def fetchRates(): EitherT[F, NetworkError, RateList] =

        loader.mapK(eitherTWrapper).fetchRates(uri"https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml")
    }

  private def eitherTWrapper[F[_]](implicit F: Sync[F]): FunctionK[F, EitherT[F, NetworkError, *]] =
    new FunctionK[F, EitherT[F, NetworkError, *]] {
      def apply[A](fa: F[A]): EitherT[F, NetworkError, A] = {
        F.attemptT(fa).leftSemiflatMap{
          case ne: NetworkError => F.pure(ne)
          case e  => F.raiseError(e)
        }
      }
    }

  private implicit def networkErrorRaise[F[_]](implicit F: Sync[F]): Raise[F, NetworkError] =
    new Raise[F, NetworkError] {
      def functor: Functor[F] = F

      def raise[E2 <: NetworkError, A](e: E2): F[A] = F.raiseError(e)
    }
}
