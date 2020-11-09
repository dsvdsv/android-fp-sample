package com.github.dsvdsv.fp.sample
package runtime
package ui

import java.time.LocalDate

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.{LinearLayoutManager, RecyclerView}
import cats.effect.{IO, Resource}
import com.github.dsvdsv.fp.sample.domain.net.{NetworkError, RateList}
import com.google.android.material.snackbar.{BaseTransientBottomBar, Snackbar}

class MainActivity extends AppCompatActivity with ApplicationLookup {
  private var loader: ProgressBar              = _
  private var list: RecyclerView               = _
  private var adapter: RateListRecyclerAdapter = _

  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    loader = findViewById(R.id.loader)
    list = findViewById(R.id.list)

    list.setHasFixedSize(true);
    list.setLayoutManager(new LinearLayoutManager(this))

    adapter = new RateListRecyclerAdapter(RateList(LocalDate.now(), List.empty))
    list.setAdapter(adapter)
  }

  override def onResume(): Unit = {
    super.onResume()

    val loadind = Resource.make(showLoading())(_ => hideLoading())

    loadind
      .use { _ =>
        sampleApplication().applicationRuntime.flatMap { runtime =>
          runtime.fetchRates().foldF[Unit](showError, showRateList)
        }
      }
      .unsafeRunSync()
  }

  private def showLoading(): IO[Unit] =
    uiThread { loader.setVisibility(View.VISIBLE) }

  private def hideLoading(): IO[Unit] =
    uiThread { loader.setVisibility(View.GONE) }

  private def showRateList(rateList: RateList): IO[Unit] =
    uiThread {
      adapter.rateList = rateList
      adapter.notifyDataSetChanged()
    }

  private def showError(error: NetworkError): IO[Unit] =
    uiThread {
      Snackbar.make(list, error.getMessage, BaseTransientBottomBar.LENGTH_SHORT).show()
    }

  private def uiThread(body: => Unit): IO[Unit] =
    IO {
      runOnUiThread(new Runnable {
        def run(): Unit =
          body
      })
    }
}
