package com.github.dsvdsv.fp.sample.runtime

import android.app.Application
import cats.effect.{ExitCode, IO, IOApp}
import com.github.dsvdsv.fp.sample.domain.UIApi

class RateApplication extends Application with IOApp {
  var runtime: IO[(UIApi[IO], IO[Unit])] = _

  override def onCreate(): Unit = {
    super.onCreate()
    runtime = Runtime.uiApi[IO].allocated
  }

  override def onTerminate(): Unit = {
    super.onTerminate()

    if (runtime != null) {
      runtime.flatMap(_._2).unsafeRunSync()
    }
  }

  def applicationRuntime: IO[UIApi[IO]] =
    runtime.map(_._1)

  override def run(args: List[String]): IO[ExitCode] =
    IO.pure(ExitCode.Success)
}

