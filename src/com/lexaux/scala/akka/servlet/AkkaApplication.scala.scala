package com.lexaux.scala.akka.servlet

import javax.servlet.{ServletContextEvent, ServletContextListener}
import akka.actor.ActorSystem
import grizzled.slf4j.Logging

/**
 * Akka application setup here.
 */
class AkkaServletContextListener extends ServletContextListener with Logging {

  def contextDestroyed(p1: ServletContextEvent) {
    info("Hello! Staring the demolition.")
    AkkaApplication.actorSystem.shutdown()
    AkkaApplication.actorSystem.awaitTermination()
    info("Hello! Destroyed.")
  }

  def contextInitialized(p1: ServletContextEvent) {
    AkkaApplication.actorSystem = ActorSystem("RestAkkaApplication")
    info("Hello! Created.")
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