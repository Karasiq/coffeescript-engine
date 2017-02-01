name := "coffeescript"

organization := "com.github.karasiq"

version := "1.0.2"

isSnapshot := version.value.endsWith("SNAPSHOT")

scalaVersion := "2.12.1"

crossScalaVersions := Seq("2.11.8", "2.12.1")

libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.0.1" % "test"
)

publishMavenStyle := true

publishTo := {
  val nexus = "https://oss.sonatype.org/"
  if (isSnapshot.value)
    Some("snapshots" at nexus + "content/repositories/snapshots")
  else
    Some("releases" at nexus + "service/local/staging/deploy/maven2")
}

publishArtifact in Test := false

pomIncludeRepository := { _ ⇒ false }

licenses := Seq("The MIT License" → url("http://opensource.org/licenses/MIT"))

homepage := Some(url("https://github.com/Karasiq/coffeescript-engine"))

pomExtra := <scm>
  <url>git@github.com:Karasiq/coffeescript-engine.git</url>
  <connection>scm:git:git@github.com:Karasiq/coffeescript-engine.git</connection>
</scm>
  <developers>
    <developer>
      <id>karasiq</id>
      <name>Piston Karasiq</name>
      <url>https://github.com/Karasiq</url>
    </developer>
  </developers>