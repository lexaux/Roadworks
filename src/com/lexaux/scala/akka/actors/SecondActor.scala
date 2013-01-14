package com.lexaux.scala.akka.actors

import akka.event.Logging
import akka.actor.Actor
import com.lexaux.scala.model.Data

/**
 * Another actor.
 */
class SecondActor extends Actor {

  val log = Logging(context.system, this)
  val creationTime = System.currentTimeMillis()
  log.info("Created SecondActor " + this)

  def receive = {
    case "Ping2" => sender ! new Data(17)
    case _ => log.warning("DONT KNOW HOW TO HANDLE")
  }
}
