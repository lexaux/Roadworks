name := "Roadworks REST api"

version := "0.1"

scalaVersion := "2.10.0"

libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % "1.0.0-RC2",
  "org.slf4j" % "slf4j-jdk14" % "1.7.2",
  "org.clapper" % "grizzled-slf4j_2.10" % "1.0.1",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.sun.jersey" % "jersey-server" % "1.16",
  "com.sun.jersey" % "jersey-core" % "1.16",
  "com.sun.jersey" % "jersey-json" % "1.16",
  "com.sun.jersey" % "jersey-servlet" % "1.16",
  "com.typesafe.akka" %% "akka-actor" % "2.1.0",
  "javax.servlet" % "servlet-api" % "2.5",
  "javax.ws.rs" % "jsr311-api" % "1.1"
)
