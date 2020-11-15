package com.github.dsvdsv.fp.sample.domain

import javax.xml.parsers.SAXParserFactory
import org.http4s.scalaxml.ElemInstances

package object net extends ElemInstances {
  override val saxFactory: SAXParserFactory = {
    val factory = SAXParserFactory.newInstance
    factory.setValidating(false)
    factory
  }
}
