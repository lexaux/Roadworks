package com.augmentari.roadworks.rest.akka.actors

import
akka.actor.{Actor, ActorRef, Props}
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

case class DBData(t: Date, s: Double, la: Double, lo: Double, sp: Double) {
  var Time: Date = t
  var Severity: Double = s
  var Latitude: Double = la
  var Longitude: Double = lo
  var Speed: Double = sp

  override def toString = Time + ","+ Severity + "," + Latitude + "," + Longitude + "," + Speed
}

case class SplitedArray(split: Array[DBData])

case class Succes(succ: String)

class SecondActor extends Actor with grizzled.slf4j.Logging {

  val splitActor = context.actorOf(Props[SplitToArrayActor], name = "split")
  val workWithDBActor = context.actorOf(Props[WorkWithDBActor], name = "workWith")

  val creationTime = System.currentTimeMillis()

  def receive = {
    case SessionsReceived(x) => {
      Database.forURL("jdbc:postgresql://127.0.0.1/slicktest", "postgres", "postgres", driver = "org.postgresql.Driver") withSession {
        val res = RecordingSessions.insertAll(x.map(s => (s.getId, s.getStartTime, s.getEndTime, s.getEventsLogged)).toSeq: _*)
//        sender ! SessionsReceivedResp(res getOrElse(0))
        info("Processed")
      }
      for (data <- x)
        splitActor ! data.getData()
    }
    case SplitedArray(split) =>
      workWithDBActor ! split
    case Succes(succ) =>
      info(succ)

    case _ => warn("DONT KNOW HOW TO HANDLE")
  }
}
