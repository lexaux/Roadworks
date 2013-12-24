package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
import math._
//import com.augmentari.roadworks.rest.akka.actors.{DBData, SplitedArray, NormalArray}

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 21.12.13
 * Time: 13:02
 * To change this template use File | Settings | File Templates.
 */
class Normalizer extends Actor {

  def receive = {
    case SplitedArray(data) =>
      sender ! Normalization(data)
  }

  def Normalization(data: Array[DBData]): NormalArray = NormalArray{
    val dataArray = data
    var i = 0
    var j = 0
    while (j < dataArray.length) {
      if (dataArray(i).Speed == dataArray(j).Speed && dataArray(i).Latitude == dataArray(j).Latitude && dataArray(i).Longitude == dataArray(j).Longitude) {
        j += 1
      }
      else if (j - i > 1) {
        for (k <- i+1 to j-1) {
          dataArray(k).Speed = dataArray(i).Speed
          val distance = math.sqrt(math.pow(dataArray(j).Latitude - dataArray(i).Latitude, 2) + math.pow(dataArray(j).Longitude - dataArray(i).Longitude, 2))
          dataArray(k).Latitude = dataArray(k).Latitude + round(distance * ((dataArray(k).Time.getTime - dataArray(i).Time.getTime).toFloat / (dataArray(j).Time.getTime - dataArray(i).Time.getTime)))
          dataArray(k).Longitude = dataArray(k).Longitude + round(distance * ((dataArray(k).Time.getTime - dataArray(i).Time.getTime).toFloat / (dataArray(j).Time.getTime - dataArray(i).Time.getTime)))
        }
        i = j
      }
      else {
        i += 1
        j+=1
      }
    }
    dataArray
  }
}
