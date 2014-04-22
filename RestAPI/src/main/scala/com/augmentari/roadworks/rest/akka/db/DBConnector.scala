package com.augmentari.roadworks.rest.akka.db

import slick.session.{Session, Database}
import grizzled.slf4j.Logging

/**
 * Database connector.
 */
object DBConnector extends Logging {

  val ROADWORKS_JDBC_CONNECTION_STRING = System.getProperty("ROADWORKS_JDBC_CONNECTION_STRING", "jdbc:postgresql://127.0.0.1/slicktest")
  val ROADWORKS_JDBC_USERNAME = System.getProperty("ROADWORKS_JDBC_USERNAME", "postgres")
  val ROADWORKS_JDBC_PASSWORD = System.getProperty("ROADWORKS_JDBC_PASSWORD", "postgres")

  info(s"Configured to a database $ROADWORKS_JDBC_CONNECTION_STRING")

  /**
   * Runs method in a database
   * @param f a function executed within DB context
   */
  def withDB(f: => Unit) {
    // to use these, plese add -DROADWORKS_JDBC_USERNAME to JAVA_OPTS of a tomcat!
    Database.forURL(ROADWORKS_JDBC_CONNECTION_STRING, ROADWORKS_JDBC_USERNAME, ROADWORKS_JDBC_PASSWORD, driver = "org.postgresql.Driver")
      .withSession(f)
  }

  /**
   * An empty and easy 'ping' method to load an object and tell if we are already loaded.
   */
  def ping() {
    info("Pong from DBConnector")
  }


}
