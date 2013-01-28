package com.augmentari.roadworks.rest.akka.actors

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
class FirstActorWithLogging extends Actor with grizzled.slf4j.Logging {
  def receive = {
    case "Ping" => {
      implicit val timeout = Timeout(4, TimeUnit.SECONDS)
      info("received Ping")
    }
    case "Pong" => info("PONG")

    case _ => info("received unknown")
  }
}
