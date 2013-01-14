package com.lexaux.java.rest;

import com.lexaux.scala.model.Data;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

/**
 */
@Path("/helloworld")
@Produces("application/json")
public class RestfulJavaSample {

    @GET
    @Path("{id}")
    public Data getObjectById(@PathParam("id") String id) {
        return new Data(2);
    }

}

