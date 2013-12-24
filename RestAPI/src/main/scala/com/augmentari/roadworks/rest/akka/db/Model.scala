package com.augmentari.roadworks.rest.akka.db

import java.util.Date
import scala.slick.driver.PostgresDriver.simple._
import slick.lifted.BaseTypeMapper
import slick.driver.BasicProfile
import slick.session.{PositionedResult, PositionedParameters}
import java.sql


trait UtilDateConversion {

  class JavaUtilDateTypeMapperDelegate extends BaseTypeMapper[java.util.Date] with TypeMapperDelegate[java.util.Date] {
    def apply(p: BasicProfile) = this

    def zero = new java.util.Date(0L)

    def sqlType = java.sql.Types.TIMESTAMP

    def setValue(v: java.util.Date, p: PositionedParameters) {
      p.setDate(new sql.Date(v.getTime))
    }

    def setOption(v: Option[Date], p: PositionedParameters) {
      p.setDateOption(v.map(d => new sql.Date(d.getTime)))
    }

    def nextValue(r: PositionedResult) = new
        java.util.Date(r.nextTimestamp().getTime)

    def updateValue(v: java.util.Date, r: PositionedResult) {
      r.updateTimestamp(new java.sql.Timestamp(v.getTime))
    }

    override def valueToSQLLiteral(value: java.util.Date) = "{ts '" + new
        java.sql.Timestamp(value.getTime).toString + "'}"

    def sqlTypeName = "timestamp"
  }

  implicit val utilDelegate = new JavaUtilDateTypeMapperDelegate
}

/**
 * Akka application setup here.
 */
object RecordingSessions extends Table[(Long, Long, Date, Date, Long)]("recording_session") with UtilDateConversion {

  def userId = column[Long]("user_id")

  def id = column[Long]("id")

  def startTime = column[Date]("start_time", O.NotNull)

  def endTime = column[Date]("end_time", O.NotNull)

  def eventsLogged = column[Long]("events_logged", O.NotNull)

  def * = userId ~ id ~ startTime ~ endTime ~ eventsLogged

  def pk = primaryKey("pk_key", (userId, id))
}

//case class PointsClass(id: Option[Int], time: Date, sensor1: Double, sensor2: Double, sensor3: Double, speed: Double, latitude: Double, longitude: Double)

object Points extends Table[(Date, Double, Double, Double, Double, Double, Double)]("points") with  UtilDateConversion {

  //def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

  def time = column[Date]("time", O.NotNull)

  def sensor1 = column[Double]("sensor1", O.NotNull)

  def sensor2 = column[Double]("sensor2", O.NotNull)

  def sensor3 = column[Double]("sensor3", O.NotNull)

  def speed = column[Double]("speed", O.NotNull)

  def latitude = column[Double]("latitude", O.NotNull)

  def longitude = column[Double]("longitude", O.NotNull)

  def * = time ~ sensor1 ~ sensor2 ~ sensor3 ~ speed ~ latitude  ~ longitude

  def pk = primaryKey("pk_Points", (sensor1, sensor2, sensor3, speed, latitude, longitude))
}