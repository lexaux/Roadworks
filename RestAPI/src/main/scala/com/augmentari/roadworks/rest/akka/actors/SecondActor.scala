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
      Database.forURL("jdbc:postgresql://localhost/slicktest", "username", "password", driver = "org.postgresql.Driver") withSession {
        val sessions = for (sess <- x if sess.getId != null) yield
          (1L, sess.getId, sess.getStartTime, sess.getEndTime, sess.getEventsLogged)

        sender ! SessionsReceivedResp(RecordingSessions.insertAll(sessions.toSeq: _*) getOrElse 0)
        info("Processed")
      }
    }
    case _ => warn("DONT KNOW HOW TO HANDLE")
  }
}
