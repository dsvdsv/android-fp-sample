package com.github.dsvdsv.fp.sample.runtime.util

import java.util.concurrent.{Executor, ExecutorService, Executors}

import android.os.{Handler, Looper}
import cats.Applicative
import cats.effect.{Resource, Sync}

import scala.concurrent.ExecutionContext

object ExecutionContexts {
  def fixedThreadPool[F[_]](size: Int)(
      implicit F: Sync[F]
  ): Resource[F, ExecutionContext] = {
    val alloc = F.delay(Executors.newFixedThreadPool(size))
    val free  = (es: ExecutorService) => F.delay(es.shutdown())
    Resource.make(alloc)(free)
      .map(ExecutionContext.fromExecutor)
  }

  def cachedThreadPool[F[_]](
      implicit F: Sync[F]
  ): Resource[F, ExecutionContext] = {
    val alloc = F.delay(Executors.newCachedThreadPool)
    val free  = (es: ExecutorService) => F.delay(es.shutdown())
    Resource.make(alloc)(free)
      .map(ExecutionContext.fromExecutor)
  }

  def mainUiThreadPool[F[_]](implicit F: Sync[F]): Resource[F, ExecutionContext] = {
    val alloc = F.delay(new Handler(Looper.getMainLooper()))
    val free  = F.unit
    Resource
      .make(alloc)(_ => free)
      .map { handler =>
        new Executor {
          override def execute(command: Runnable): Unit = {
            handler.post(command)
            ()
          }
        }
      }
      .map(ExecutionContext.fromExecutor)
  }

  // for testing
  def synchronousThreadPool[F[_]: Applicative]: Resource[F, ExecutionContext] =
    Resource.pure(synchronous)

  // for testing
  private object synchronous extends ExecutionContext {
    def execute(runnable: Runnable): Unit     = runnable.run()
    def reportFailure(cause: Throwable): Unit = cause.printStackTrace()
  }
}
