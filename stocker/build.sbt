name := """stocker"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  javaWs,
  "com.datastax.cassandra" % "cassandra-driver-core" % "3.0.1"
)

libraryDependencies += "com.typesafe.akka" % "akka-actor_2.11" % "2.4.8"