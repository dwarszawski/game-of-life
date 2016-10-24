package org.dwarszawski.game

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.util.Timeout

import scala.concurrent.duration._

object GameApp {

  implicit val timeout: Timeout = 3 seconds

  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem("AsyncGameOfLife")
    implicit val materializer: ActorMaterializer = ActorMaterializer()

    val board = system.actorOf(Coordinator.props())

    board ! CoordinatorCommands.Start
  }
}
