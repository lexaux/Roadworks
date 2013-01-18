package com.lexaux.scala.rest

import javax.ws.rs.{GET, PathParam, Produces, Path}
import akka.pattern.ask
import com.lexaux.scala.akka.actors.SecondActor
import akka.actor.Props
import concurrent.Await
import akka.util.Timeout
import java.util.concurrent.TimeUnit
import com.lexaux.scala.akka.servlet.AkkaApplication
import com.lexaux.scala.model.Data

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
    val future = actor ? "Ping2"
    Await.result(future, timeout.duration)
  }
}
