package com.github.dsvdsv.fp.sample
package runtime
package ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class MainActivity extends AppCompatActivity with ApplicationLookup {
  override protected def onCreate(savedInstanceState: Bundle): Unit = {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }
}