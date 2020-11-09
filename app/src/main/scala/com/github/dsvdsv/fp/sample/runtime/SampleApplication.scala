package com.github.dsvdsv.fp.sample.runtime

import android.app.Application
import cats.effect.{ExitCode, IO, IOApp}
import com.github.dsvdsv.fp.sample.runtime.context.ApplicationRuntime

class SampleApplication extends Application {
  import App._
  override def onCreate(): Unit = {
    super.onCreate()
  }

  override def onTerminate(): Unit = {
    super.onTerminate()

    if (runtime != null) {
      runtime.flatMap(_._2).unsafeRunSync()
    }
  }

  def applicationRuntime: IO[ApplicationRuntime[IO]] =
    runtime.map(_._1)
}

object App extends IOApp {
  val runtime: IO[(ApplicationRuntime[IO], IO[Unit])] =
    ApplicationRuntime.launch[IO].allocated

  override def run(args: List[String]): IO[ExitCode] =
    IO.pure(ExitCode.Success)
}