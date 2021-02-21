package com.github.dsvdsv.fp.sample.domain.net

import cats.effect.Sync
import cats.mtl.Raise
import cats.syntax.all._
import cats.tagless.{autoFunctorK, finalAlg}
import org.http4s.client.Client
import org.http4s.{Request, Status, Uri}

import java.io.IOException

@finalAlg
@autoFunctorK
trait ExchangeClient[F[_]] {
  def fetch(uri: Uri): F[RateList]
}

object ExchangeClient {
  def fromClient[F[_]](
      client: Client[F]
  )(implicit F: Sync[F], R: Raise[F, ExchangeError]): F[ExchangeClient[F]] = {
    import ExchangeError._

    F.pure(
      new ExchangeClient[F] {
        override def fetch(uri: Uri): F[RateList] = {
          val req = Request[F](uri = uri)
          val program = client
            .run(req)
            .use { resp =>
              resp.status match {
                case Status.Ok =>
                  resp
                    .attemptAs[RateList]
                    .foldF(e => R.raise[ExchangeError, RateList](decodeError(e.getMessage())), _.pure[F])
                case Status.NotFound =>
                  R.raise[ExchangeError, RateList](notFound(uri.toString() + " not found"))
                case s =>
                  R.raise[ExchangeError, RateList](serverError("Unknown status " + s))
              }
            }

          F.recoverWith(program) {
            case ex: IOException => R.raise(communicationError(ex.getMessage))
          }
        }
      }
    )
  }
}
