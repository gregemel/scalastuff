name := "scalastuff"

version := "0.1"

scalaVersion := "2.11.8"

resolvers ++= Seq(
  "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"
)

libraryDependencies ++= Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.4.0",
  "com.typesafe.akka" %% "akka-stream" % "2.4.7",
  "org.scalatest" %% "scalatest" % "3.0.0" % "test",
  "org.mockito" % "mockito-all" % "1.10.19" % "test",
  "com.typesafe.akka" %% "akka-stream-testkit" % "2.4.7" % "test"
)