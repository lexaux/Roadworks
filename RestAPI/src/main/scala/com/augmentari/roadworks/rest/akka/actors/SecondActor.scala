package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
import com.augmentari.roadworks.model.RecordingSession

/**
 * Another actor.
 */
case class TestMessage(num: Int)

class SecondActor extends Actor with grizzled.slf4j.Logging {

  val creationTime = System.currentTimeMillis()

  def receive = {
    case TestMessage(x) => {
      warn("INVOKING SecondActor with message " + x)
      sender ! new RecordingSession()
    }
    case _ => warn("DONT KNOW HOW TO HANDLE")
  }
}
