package com.augmentari.roadworks.rest.rest

import javax.ws.rs.ext.{ContextResolver, Provider}
import org.codehaus.jackson.map.ObjectMapper
import org.codehaus.jackson.map.annotate.JsonSerialize

/**
 * Provider of the default OBjectMapperskipping null values.
 */
@Provider
class ObjectMapperProvider extends ContextResolver[ObjectMapper] {
  val defaultObjectMapper = new ObjectMapper()
  defaultObjectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)

  def getContext(`type`: Class[_]) = defaultObjectMapper
}
