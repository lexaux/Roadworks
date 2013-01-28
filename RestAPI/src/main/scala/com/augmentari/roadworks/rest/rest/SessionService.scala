package com.augmentari.roadworks.rest.rest

import javax.ws.rs._
import akka.pattern.ask
import akka.actor.Props
import concurrent.Await
import com.augmentari.roadworks.rest.akka.servlet.AkkaApplication
import com.augmentari.roadworks.rest.akka.actors.{TestMessage, SecondActor}
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
    val actor = AkkaApplication.getSystem.actorOf(Props[SecondActor])
    val future = actor ? TestMessage(augmentString(id).toInt)
    Await.result(future, timeout.duration)
  }

  @POST
  def postObjects() = {
    val actor = AkkaApplication.getSystem.actorOf(Props[SecondActor])
    val future = actor ? TestMessage(0)
    Await.result(future, timeout.duration)
  }
}
