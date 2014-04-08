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
object RecordingSessions extends Table[(Long, Date, Date, Long)]("recording_session") with UtilDateConversion {

  def id = column[Long]("id")

  def startTime = column[Date]("start_time", O.NotNull)

  def endTime = column[Date]("end_time", O.NotNull)

  def eventsLogged = column[Long]("events_logged", O.NotNull)

  def * = id ~ startTime ~ endTime ~ eventsLogged

  def pk = primaryKey("pk_key", (id, startTime))
}

case class PointsClass(id: Option[Int], time: Date, severity: Double, latitude: Double, longitude: Double, speed: Double)

object Points extends Table[PointsClass]("points") with UtilDateConversion {

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def time = column[Date]("add_time", O.NotNull)

  def severity = column[Double]("severity", O.NotNull)

  def latitude = column[Double]("latitude", O.NotNull)

  def longitude = column[Double]("longitude", O.NotNull)

  def speed = column[Double]("speed", O.NotNull)

  def * = id.? ~ time ~ severity ~ latitude ~ longitude ~ speed <>(PointsClass, PointsClass.unapply _)

  def insrt = time ~ severity ~ latitude ~ longitude ~ speed returning id
}

