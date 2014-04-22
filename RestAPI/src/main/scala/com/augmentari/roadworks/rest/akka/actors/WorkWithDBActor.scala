package com.augmentari.roadworks.rest.akka.actors

import
akka.actor.Actor

//import com.augmentari.roadworks.rest.akka.actors.{DBData, PointedArray}

import slick.session.Database
import com.augmentari.roadworks.rest.akka.db.{DBConnector, Points}
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 24.12.13
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */

class WorkWithDBActor extends Actor with grizzled.slf4j.Logging {

  def receive = {
    case split: Array[DBData] => {
      connectionToDB(split)
      sender ! Succes("Succes!")
    }
    case _ => info("Type of variable is not correct: SplitedArray")
  }

  def connectionToDB(pointArray: Array[DBData]) = {
    DBConnector.withDB {
      for (point <- pointArray)
        Points.insrt.insert(point.Time, point.Severity, point.Latitude, point.Longitude, point.Speed)
    }
  }
}
