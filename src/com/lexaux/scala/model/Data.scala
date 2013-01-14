package com.lexaux.scala.model

import javax.xml.bind.annotation.{XmlElement, XmlRootElement}

/**
 * A sample data object
 */
@XmlRootElement
class Data(id: Int) {

  @XmlElement
  var id1: Int = id

  @XmlElement
  var x: String = null

  def this() = this(1)
}

