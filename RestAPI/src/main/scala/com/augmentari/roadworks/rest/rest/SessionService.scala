package com.augmentari.roadworks.rest.rest

import javax.ws.rs._
import akka.actor.Props
import com.augmentari.roadworks.rest.akka.servlet.AkkaApp
import com.augmentari.roadworks.rest.akka.actors.{SessionsReceived, SecondActor}
import com.augmentari.roadworks.model.RecordingSession

/**
 * Sample REST service.
 * Will see how it goes.
 */
@Path("helloworld")
@Produces(Array("application/json"))
class SessionService extends ServiceConstants {

  @GET
  @Path("{id}")
  def getObjectById(@PathParam("id") id: String) = {
    "Hello!"
  }

  @POST
  @Consumes(Array("application/json"))
  @Produces(Array("application/json"))
  def postObjects(recordingSessions: Array[RecordingSession]) {
    AkkaApp().actorOf(Props[SecondActor]) ! SessionsReceived(recordingSessions.toList)
  }
}
