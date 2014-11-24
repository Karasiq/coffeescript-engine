name := "coffeescript"

organization := "com.karasiq"

version := "1.0"

scalaVersion := "2.11.6"

scalacOptions ++= Seq("-feature", "-optimize")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "2.2.1" % "test"
)