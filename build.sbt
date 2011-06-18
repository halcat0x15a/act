name := "ACT"

version := "1.0"

organization := "baskingcat"

scalaVersion := "2.9.0-1"

libraryDependencies ++= Seq(
"org.scalaz" %% "scalaz-core" % "6.0.1",
"org.scala-tools.testing" %% "scalacheck" % "1.9"
)

scalacOptions ++= Seq("-deprecation", "-unchecked")
