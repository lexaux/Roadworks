package com.lexaux.scala.akka.actors

import akka.actor.{Props, Actor}
import akka.event.Logging
import akka.pattern.{ask, pipe}
import concurrent.Await
import akka.util.Timeout
import java.util.concurrent.TimeUnit


/**
 * The first actor to try.
 * Should be invoked from where?
 */
class FirstActorWithLogging extends Actor {
  val log = Logging(context.system, this)

  def receive = {
    case "Ping" => {
      implicit val timeout = Timeout(4, TimeUnit.SECONDS)
      log.info("received Ping")
    }
    case "Pong" => log.info("PONG")

    case _ => log.info("received unknown")
  }
}
