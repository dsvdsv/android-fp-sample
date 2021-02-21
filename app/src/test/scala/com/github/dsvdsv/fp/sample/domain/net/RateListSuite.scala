package com.github.dsvdsv.fp.sample.domain.net

import java.time.LocalDate

import cats.effect.IO
import cats.implicits._
import munit.CatsEffectSuite
import org.http4s.{InvalidMessageBodyFailure, Response}
import org.http4s.Status.Ok

class RateListSuite extends CatsEffectSuite {
  test("success parse xml") {
    val xmlBody =
      <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
          <Cube>
            <Cube time='2020-11-03'>
              <Cube currency='USD' rate='1.1702'/>
              <Cube currency='JPY' rate='122.56'/>
            </Cube>
          </Cube>
        </gesmes:Envelope>

    val expected =
      RateList(
        LocalDate.of(2020, 11, 3),
        List(
          RateList.Rate("USD", BigDecimal("1.1702")),
          RateList.Rate("JPY", BigDecimal("122.56"))
        )
      )

    val program = for {
      resp <- Response[IO](Ok).withEntity(xmlBody).pure[IO]
      res  <- RateList.fromXml[IO].decode(resp, false).value
    } yield res

    program.assertEquals(Right(expected))
  }

  test("fail when attempt to parse wrong xml") {
    val xmlBody =
      <gesmes:Envelope xmlns:gesmes="http://www.gesmes.org/xml/2002-08-01" xmlns="http://www.ecb.int/vocabulary/2002-08-01/eurofxref">
        <Cube>
          <Cube time='boom'>
            <Cube currency='USD' rate='1.1702'/>
          </Cube>
        </Cube>
      </gesmes:Envelope>

    val program = for {
      resp <- Response[IO](Ok).withEntity(xmlBody).pure[IO]
      res  <- RateList.fromXml[IO].decode(resp, false).rethrowT
    } yield res

    program.intercept[InvalidMessageBodyFailure]
  }
}
