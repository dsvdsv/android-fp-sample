package com.github.dsvdsv.fp.sample.domain

import cats.Applicative
import cats.data.EitherT
import cats.effect.Sync
import cats.mtl.Handle
import cats.syntax.all._
import cats.tagless.{autoFunctorK, finalAlg}
import org.http4s.client.Client

import config.Config
import net.{ExchangeClient, ExchangeError, RateList}

@finalAlg
@autoFunctorK
trait UIApi[F[_]] {
  def fetchRates(): EitherT[F, ExchangeError, RateList]
}

object UIApi {
  def make[F[_]](config: Config, client: Client[F])(implicit F: Sync[F]): F[UIApi[F]] = {
    implicit val errorHandler: Handle[F, ExchangeError] =
      new Handle[F, ExchangeError] {
        def applicative: Applicative[F] = F

        def handleWith[A](fa: F[A])(f: ExchangeError => F[A]): F[A] =
          F.recoverWith(fa) {
            case e: ExchangeError => f(e)
          }

        def raise[E2 <: ExchangeError, A](e: E2): F[A] = F.raiseError(e)
      }

    ExchangeClient.fromClient(client).map { exchClient =>
      new UIApi[F] {
        def fetchRates(): EitherT[F, ExchangeError, RateList] =
          errorHandler.attemptT(exchClient.fetch(config.uri))
      }
    }
  }
}
