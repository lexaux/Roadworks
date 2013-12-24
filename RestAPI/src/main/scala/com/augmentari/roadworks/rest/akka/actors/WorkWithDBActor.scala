package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor

//import com.augmentari.roadworks.rest.akka.actors.{DBData, PointedArray}

import slick.session.Database
import com.augmentari.roadworks.rest.akka.db.Points
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 24.12.13
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */

class WorkWithDBActor extends Actor {

  def receive = {
    case PointedArray(pointArray) => {
      connectionToDB(pointArray)
      sender ! Succes("Succes!")
    }
  }

  def connectionToDB(pointArray: Array[DBData]) = {
    Database.forURL("jdbc:postgresql://127.0.0.1/slicktest", "postgres", "postgres", driver = "org.postgresql.Driver") withSession {
      for (point <- pointArray)
        Points.insert(point.Time, point.Sensor1, point.Sensor2, point.Sensor3, point.Speed, point.Latitude, point.Longitude)
    }
  }
}
