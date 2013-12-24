package com.augmentari.roadworks.rest.rest

import javax.ws.rs._
import akka.actor.Props
import akka.pattern.ask
import com.augmentari.roadworks.rest.akka.servlet.AkkaApp
import com.augmentari.roadworks.rest.akka.actors.{SessionsReceivedResp, SessionsReceived, SecondActor}
import com.augmentari.roadworks.model.RecordingSession
import concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import com.augmentari.roadworks.rest.akka.actors.Succes

/**
 * Sample REST service.
 * Will see how it goes.
 */
@Path("helloworld")
@Produces(Array("application/json"))
class SessionService extends ServiceConstants with grizzled.slf4j.Logging {

  @GET
  @Path("{id}")
  def getObjectById(@PathParam("id") id: String) = {
    "Hello!"
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  def postObjects(recordingSessions: Array[RecordingSession]) {
    var secondActor = AkkaApp().actorOf(Props[SecondActor])
    secondActor ! SessionsReceived(recordingSessions.toList)
    var future = secondActor ? Succes
    var res = Await.result(future, timeout.duration)//.asInstanceOf[SessionsReceivedResp]


    /*future onSuccess {
      case SessionsReceivedResp(count) => info("Saved " + res.inserted + "  results")
      case _ => info("Got wrong results")
    }*/
    info("sending response")
  }
}
