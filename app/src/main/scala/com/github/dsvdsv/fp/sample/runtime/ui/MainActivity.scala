package com.github.dsvdsv.fp.sample
package runtime
package ui

import java.time.LocalDate
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.{LinearLayoutManager, RecyclerView}
import cats.effect.{IO, Resource}
import com.google.android.material.snackbar.{BaseTransientBottomBar, Snackbar}

import domain.net.{ExchangeError, RateList}

class MainActivity extends AppCompatActivity with ApplicationLookup {
  private var loader: ProgressBar              = _
  private var list: RecyclerView               = _
  private var adapter: RateListRecyclerAdapter = _

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    loader = findViewById(R.id.loader)
    list = findViewById(R.id.list)

    list.setHasFixedSize(true)
    list.setLayoutManager(new LinearLayoutManager(this))

    adapter = new RateListRecyclerAdapter(RateList(LocalDate.now(), List.empty))
    list.setAdapter(adapter)
  }

  override def onResume(): Unit = {
    super.onResume()

    val loading = Resource.make(showLoading())(_ => hideLoading())

    loading
      .use { _ =>
        rateApplication().applicationRuntime.flatMap { runtime =>
          runtime.fetchRates().foldF[Unit](showError, showRateList)
        }
      }
      .unsafeRunAsync({
        case Left(er) =>
          Log.e("MainActivity", "Error " + Thread.currentThread().getName(), er)
          ()
        case Right(_) =>
          Log.d("MainActivity", "Success " + Thread.currentThread().getName())
          ()
      })
  }

  def showLoading(): IO[Unit] =
    uiThread {
      loader.setVisibility(View.VISIBLE)
    }

  def hideLoading(): IO[Unit] =
    uiThread {
      loader.setVisibility(View.GONE)
    }

  def showRateList(rateList: RateList): IO[Unit] =
    uiThread {
      adapter.rateList = rateList
      adapter.notifyDataSetChanged()
    }

  def showError(error: ExchangeError): IO[Unit] =
    uiThread {
      Snackbar.make(list, error.getMessage, BaseTransientBottomBar.LENGTH_LONG).show()
    }

  private def uiThread(body: => Unit): IO[Unit] =
    IO {
      runOnUiThread(() => body)
    }
}
