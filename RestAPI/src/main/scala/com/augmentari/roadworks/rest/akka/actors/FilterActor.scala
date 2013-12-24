package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
import math._
//import com.augmentari.roadworks.rest.akka.actors.{DBData, NormalArray, FilteredArray}

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 21.12.13
 * Time: 13:50
 * To change this template use File | Settings | File Templates.
 */
class FilterActor extends Actor {

  def receive: Receive = {
    case NormalArray(norm) =>
      sender ! LowPass(norm)
  }

  def LowPass(data: Array[DBData]): FilteredArray = FilteredArray{
    val FilterArray = data
    val timeConstant = 0.297
    var dt = 0.0
    var alpha = 0.0
    /* var i = 0*/
    for (i <- 1 to FilterArray.length - 1) {
      dt = (FilterArray(i).Time.getTime - FilterArray(i - 1).Time.getTime) / 1000000000.0
      alpha = timeConstant / (timeConstant + dt)
      FilterArray(i).Sensor1 = round(alpha * FilterArray(i).Sensor1 + (1 - alpha) * FilterArray(i - 1).Sensor1)
      FilterArray(i).Sensor2 = round(alpha * FilterArray(i).Sensor2 + (1 - alpha) * FilterArray(i - 1).Sensor2)
      FilterArray(i).Sensor3 = round(alpha * FilterArray(i).Sensor3 + (1 - alpha) * FilterArray(i - 1).Sensor3)
    }
    FilterArray
  }
}

