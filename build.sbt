name := "ACT"

version := "1.0"

organization := "baskingcat"

scalaVersion := "2.9.0-1"

resolvers += "okomok releases" at "http://okomok.github.com/maven-repo/releases"

libraryDependencies ++= Seq(
"com.github.okomok" % "sing_2.9.0" % "0.1.0",
"org.scalaz" %% "scalaz-core" % "6.0.1",
"org.scala-tools.testing" %% "scalacheck" % "1.9"
)

scalacOptions ++= Seq("-deprecation", "-unchecked")
