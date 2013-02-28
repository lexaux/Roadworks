package com.augmentari.roadworks.rest.akka.servlet

import scala.slick.driver.PostgresDriver.simple._
import akka.actor.ActorSystem
import grizzled.slf4j.Logging
import slick.session.Database
import javax.servlet.{ServletContextEvent, ServletContextListener}

import Database.threadLocalSession
import com.augmentari.roadworks.rest.akka.db.RecordingSessions

class AkkaServletContextListener extends ServletContextListener with Logging {


  def createDB() {
    Database.forURL("jdbc:postgresql://127.0.0.1/slicktest", "username", "password", driver = "org.postgresql.Driver") withSession {
      try {
        RecordingSessions.ddl.drop
      } finally {
        RecordingSessions.ddl.create
      }
    }
  }

  createDB()

  def contextDestroyed(p1: ServletContextEvent) {
    AkkaApp.actorSystem.shutdown()
    AkkaApp.actorSystem.awaitTermination()
  }

  def contextInitialized(p1: ServletContextEvent) {
    AkkaApp.actorSystem = ActorSystem("RestAkkaApplication")
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