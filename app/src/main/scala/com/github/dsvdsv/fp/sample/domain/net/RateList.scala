package com.github.dsvdsv.fp.sample.domain.net

import java.time.LocalDate

import cats.data.EitherT
import cats.effect.Sync
import cats.implicits._
import org.http4s.{EntityDecoder, InvalidMessageBodyFailure}

import scala.util.control.NonFatal
import scala.xml.Elem

final case class RateList(time: LocalDate, rates: List[RateList.Rate])

object RateList {
  final case class Rate(currency: String, rate: BigDecimal)

  implicit def fromXml[F[_]](implicit F: Sync[F], E: EntityDecoder[F, Elem]): EntityDecoder[F, RateList] =
    E.flatMapR { elem =>
      val program = F.delay {
        val time  = (elem \\ "@time").head.text
        val nodes = (elem \\ "Cube" \ "Cube" \ "Cube").toList
        val rates = nodes.map { node =>
          Rate(node \@ "currency", BigDecimal(node \@ "rate"))
        }
        RateList(
          LocalDate.parse(time),
          rates
        )
      }

      EitherT(program.attempt)
        .leftMap {
          case NonFatal(e) => InvalidMessageBodyFailure(e.getMessage, Some(e))
        }
    }
}
