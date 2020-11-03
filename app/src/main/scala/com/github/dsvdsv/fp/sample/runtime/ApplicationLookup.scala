package com.github.dsvdsv.fp.sample
package runtime

import android.content.Context

trait ApplicationLookup {
  self: Context =>

  def sampleApplication(): SampleApplication = {
    getApplicationContext().asInstanceOf[SampleApplication]
  }
}