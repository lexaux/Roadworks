package com.augmentari.roadworks.rest.akka.actors

import akka.actor.{Actor, ActorRef, Props}
import com.augmentari.roadworks.model.RecordingSession
import slick.session.Database
import com.augmentari.roadworks.rest.akka.db.RecordingSessions
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession
import java.util.{Date, Calendar}


/**
 * Another actor.
 */
case class SessionsReceived(sessions: List[RecordingSession])

case class SessionsReceivedResp(inserted: Int)

case class DBData(t: Date, s1: Double, s2: Double, s3: Double, s: Double, la: Double, lo: Double) {
  var Time: Date = t
  var Sensor1: Double = s1
  var Sensor2: Double = s2
  var Sensor3: Double = s3
  var Speed: Double = s
  var Latitude: Double = la
  var Longitude: Double = lo

  private val formatter = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")

  override def toString = Time + "," + Sensor1 + "," + Sensor2 + "," + Sensor3 +
    "," + Speed + "," + Latitude + "," + Longitude
}

case class SplitedArray(split: Array[DBData])

case class NormalArray(norm: Array[DBData])

case class FilteredArray(filter: Array[DBData])

case class PointedArray(points: Array[DBData])

case class Succes(succ: String)

class SecondActor extends Actor with grizzled.slf4j.Logging {

  val splitActor = context.actorOf(Props[SplitToArayActor], name = "split")
  val normalizerActor = context.actorOf(Props[Normalizer], name = "norm")
  val filterActor = context.actorOf(Props[FilterActor], name = "filter")
  val detectorActor = context.actorOf(Props[DetectorActor], name = "detector")
  val workWithDBActor= context.actorOf(Props[WorkWithDBActor], name = "workWith")

  val creationTime = System.currentTimeMillis()

  def receive = {
    case SessionsReceived(x) => {
      Database.forURL("jdbc:postgresql://127.0.0.1/slicktest", "postgres", "postgres", driver = "org.postgresql.Driver") withSession {
        val res = RecordingSessions.insertAll(x.map(s => (1L, s.getId, s.getStartTime, s.getEndTime, s.getEventsLogged)).toSeq: _*)
        sender ! SessionsReceivedResp(res getOrElse 0)
        info("Processed")
      }
      for (data <- x)
        splitActor ! data.getData()
    }
    case SplitedArray(split) =>
      normalizerActor ! split
    case NormalArray(data) =>
      filterActor ! data
    case FilteredArray(filter) =>
      detectorActor ! filter
    case PointedArray(points) =>
      workWithDBActor ! points
    case Succes(succ) =>
      info(succ)

    case _ => warn("DONT KNOW HOW TO HANDLE")
  }
}
