package com.augmentari.roadworks.rest.akka.actors

import akka.actor.Actor
//import com.augmentari.roadworks.rest.akka.actors.DBData
import java.util.{Date, Calendar}

/**
 * Created with IntelliJ IDEA.
 * User: Egor
 * Date: 20.12.13
 * Time: 19:05
 * To change this template use File | Settings | File Templates.
 */
class SplitToArrayActor extends Actor with grizzled.slf4j.Logging{

  def receive = {
    case data: String =>
      sender ! SplitArray(data)

    case _ => info("Type of variable is not correct: String")
  }

  def SplitArray(data: String):SplitedArray = SplitedArray {
    val formatter = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
    def getTimes(x: String): Date = {
      val milliSeconds: Long = x.toLong
      val calendar: Calendar = Calendar.getInstance()
      calendar.setTimeInMillis(milliSeconds)
      calendar.getTime()
    }
    def toDBData(data: Array[String]): DBData = new DBData(
      getTimes(data(0)),
      data(1).toDouble,
      data(2).toDouble,
      data(3).toDouble,
      data(4).toDouble
    )

    var linesArray = for (lines <- data.split('\n'))
      yield lines.split(',').toArray

   linesArray = linesArray.drop(1);

    val db = for (res <- linesArray)
      yield toDBData(res)

    db
  }
}
