package com.augmentari.roadworks.rest.akka.servlet

import scala.slick.driver.PostgresDriver.simple._
import scala.slick.driver.H2Driver.simple._
import akka.actor.ActorSystem
import grizzled.slf4j.Logging
import slick.session.Database
import javax.servlet.{ServletContextEvent, ServletContextListener}

import Database.threadLocalSession
import com.augmentari.roadworks.rest.akka.db.{DBConnector, RecordingSessions, PointsClass, Points}

class AkkaServletContextListener extends ServletContextListener with Logging {

  def contextDestroyed(p1: ServletContextEvent) {
    AkkaApp.actorSystem.shutdown()
    AkkaApp.actorSystem.awaitTermination()
  }

  def contextInitialized(p1: ServletContextEvent) {
    AkkaApp.actorSystem = ActorSystem("RestAkkaApplication")
    DBConnector.ping()
  }
}

object AkkaApp {
  def apply(): ActorSystem = getSystem

  protected[servlet] var actorSystem: ActorSystem = null

  def getSystem: ActorSystem =
    if (actorSystem == null)
      throw new IllegalArgumentException("Invoking before context initialized.")
    else
      actorSystem

}