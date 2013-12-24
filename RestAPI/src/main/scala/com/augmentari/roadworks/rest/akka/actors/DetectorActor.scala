package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
//import com.augmentari.roadworks.rest.akka.actors.{DBData, FilteredArray, PointedArray}

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 23.12.13
 * Time: 17:26
 * To change this template use File | Settings | File Templates.
 */

class DetectorActor extends Actor{

  def receive = {
    case FilteredArray(filter) =>
      sender ! detection(filter)
  }

  def detection(filter: Array[DBData]): PointedArray = PointedArray{
    def average(): Array[Double] = {
      var ave = new Array[Double](3)

      for(lines <- filter) {
        ave(0) += lines.Sensor1
        ave(1) += lines.Sensor2
        ave(2) += lines.Sensor3
      }
      ave
    }

    val averageArray = average()

    filter.filter(x => x.Sensor1 > averageArray(0) || x.Sensor2 > averageArray(1) || x.Sensor3 > averageArray(2))
  }
}