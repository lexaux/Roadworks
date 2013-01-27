package com.augmentari.roadworks.rest.rest

import javax.ws.rs.ext.{Provider, ContextResolver}
import javax.xml.bind.JAXBContext
import com.sun.jersey.api.json.{JSONConfiguration, JSONJAXBContext}
import javax.ws.rs.Produces
import com.augmentari.roadworks.rest.model.Data

/**
 * JSON serialization provider.
 */
@Provider
@Produces(Array("application/json"))
class JSONContextResolver extends ContextResolver[JAXBContext] {

  val classes = Array[Class[_]](classOf[Data])

  val context = new JSONJAXBContext(JSONConfiguration.natural().build(), classes: _*)

  def getContext(c: Class[_]) =
    if (!(classes contains c)) null
    else context

}
