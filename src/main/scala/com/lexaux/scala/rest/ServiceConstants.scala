package com.lexaux.scala.rest

import akka.util.Timeout
import java.util.concurrent.TimeUnit

/**
 * Base thing for services.
 */
trait ServiceConstants {
  implicit val timeout = new Timeout(10l, TimeUnit.SECONDS)
}
