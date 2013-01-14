package com.lexaux.scala.akka.servlet

import javax.servlet.{ServletContextEvent, ServletContextListener}
import akka.actor.ActorSystem

/**
 * Akka application setup here.
 */
class AkkaServletContextListener extends ServletContextListener {

  def contextDestroyed(p1: ServletContextEvent) {
    AkkaApplication.actorSystem.shutdown()
  }

  def contextInitialized(p1: ServletContextEvent) {
    AkkaApplication.actorSystem = ActorSystem("RestAkkaApplication")
  }
}

object AkkaApplication {

  protected[servlet] var actorSystem: ActorSystem = null

  def getSystem: ActorSystem =
    if (actorSystem == null)
      throw new IllegalArgumentException("Invoking before context initialized.")
    else
      actorSystem

}