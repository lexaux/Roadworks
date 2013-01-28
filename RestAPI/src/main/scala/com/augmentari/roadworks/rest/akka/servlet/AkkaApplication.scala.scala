package com.augmentari.roadworks.rest.akka.servlet

import scala.slick.driver.PostgresDriver.simple._
import akka.actor.ActorSystem
import grizzled.slf4j.Logging
import slick.session.Database
import javax.servlet.{ServletContextEvent, ServletContextListener}

// Use H2Driver to connect to an H2 database

// Use the implicit threadLocalSession

import Database.threadLocalSession

class AkkaServletContextListener extends ServletContextListener with Logging {

  /**
   * Akka application setup here.
   */
  object Suppliers extends Table[(Int)]("SUPPLIERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey)

    def * = id //~ name ~ street ~ city ~ state ~ zip
  }

  object Customers extends Table[(Int)]("CUSTOMERS") {
    def id = column[Int]("SUP_ID", O.PrimaryKey)

    def * = id //~ name ~ street ~ city ~ state ~ zip
  }

  def testDB() {
    Database.forURL("jdbc:postgresql://localhost/slicktest", "user", "password", driver = "org.postgresql.Driver") withSession {
      (Suppliers.ddl ++ Customers.ddl).create
    }
  }

  //  testDB()

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