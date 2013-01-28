package com.augmentari.roadworks.rest.akka.actors

import akka.event.Logging
import akka.actor.Actor
import com.augmentari.roadworks.rest.model.Data

/**
 * Another actor.
 */
case class TestMessage(num: Int)

class SecondActor extends Actor {


  val log = Logging(context.system, this)

  val creationTime = System.currentTimeMillis()

  def receive = {
    case "Ping2" => sender ! new Data(17)
    case TestMessage(x) => {
      log.warning("INVOKING SecondActor with message " + x)
      sender ! new Data(x + 1)
    }
    case _ => log.warning("DONT KNOW HOW TO HANDLE")
  }
}
