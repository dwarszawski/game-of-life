name := "async-game-of-life"

scalacOptions ++= List(
  "-unchecked",
  "-deprecation",
  "-language:_",
  "-target:jvm-1.8",
  "-encoding", "UTF-8"
)

val scalaTestV = "2.2.5"

val commonSettings = Seq(
  organization := "https://github.com/dwarszawski",
  version := "0.0.1",
  scalaVersion := "2.11.8"
)

commonSettings

libraryDependencies ++= Dependencies.core