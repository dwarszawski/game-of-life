import sbt._

object Dependencies {

  object Version {
    val akka = "2.4.11"
    val logback = "1.1.2"
  }

  object Compile {
    val akkaActor = "com.typesafe.akka" %% "akka-actor" % Version.akka
    val akkaCluster = "com.typesafe.akka" %% "akka-cluster" % Version.akka
    val akkaClusterTools = "com.typesafe.akka" %% "akka-cluster-tools" % Version.akka
    val akkaPersistence = "com.typesafe.akka" %% "akka-persistence" % Version.akka
    val akkaClusterSharding = "com.typesafe.akka" %% "akka-cluster-sharding" % Version.akka
    // pulls in akka-persistence
    val akkaTestKit = "com.typesafe.akka" %% "akka-testkit" % Version.akka

    val akkaStream = "com.typesafe.akka" %% "akka-stream" % Version.akka
    val akkaStreamTestkit = "com.typesafe.akka" %% "akka-testkit" % Version.akka
    val akkaHttpCore = "com.typesafe.akka" %% "akka-http-core" % Version.akka
    val akkaHttp = "com.typesafe.akka" %% "akka-http-testkit" % Version.akka
    val akkaMultiNodeTestKit = "com.typesafe.akka" %% "akka-multi-node-testkit" % Version.akka

    val akkaSlf4j = "com.typesafe.akka" %% "akka-slf4j" % Version.akka
    val logbackClassic = "ch.qos.logback" % "logback-classic" % Version.logback
  }

  object Test {
    val scalaTest = "org.scalatest" %% "scalatest" % "2.2.5" % "test"
    val commonsIo = "commons-io" % "commons-io" % "2.4" % "test"
  }

  import Compile._

  private val testing = Seq(Test.scalaTest, Test.commonsIo)
  private val logging = Seq(akkaSlf4j, logbackClassic)

  val core = Seq(akkaActor, akkaStream, akkaTestKit) ++ testing ++ logging
}