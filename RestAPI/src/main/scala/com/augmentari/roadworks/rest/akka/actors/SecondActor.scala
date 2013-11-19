package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
import com.augmentari.roadworks.model.RecordingSession
import slick.session.Database
import com.augmentari.roadworks.rest.akka.db.RecordingSessions
import scala.slick.driver.PostgresDriver.simple._
import Database.threadLocalSession


/**
 * Another actor.
 */
case class SessionsReceived(sessions: List[RecordingSession])

case class SessionsReceivedResp(inserted: Int)

class SecondActor extends Actor with grizzled.slf4j.Logging {

  val creationTime = System.currentTimeMillis()

  def receive = {
    case SessionsReceived(x) => {
      Database.forURL("jdbc:postgresql://localhost/slicktest", "postgres", "", driver = "org.postgresql.Driver") withSession {
        val res = RecordingSessions.insertAll(x.map(s => (1L, s.getId, s.getStartTime, s.getEndTime, s.getEventsLogged)).toSeq: _*)
        sender ! SessionsReceivedResp(res getOrElse 0)
        info("Processed")
      }
    }
    case _ => warn("DONT KNOW HOW TO HANDLE")
  }
}
