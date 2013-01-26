package com.lexaux.scala.rest

import javax.ws.rs.{GET, PathParam, Produces, Path}
import akka.pattern.ask
import com.lexaux.scala.akka.actors.{TestMessage, SecondActor}
import akka.actor.Props
import concurrent.Await
import com.lexaux.scala.akka.servlet.AkkaApplication

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
}
