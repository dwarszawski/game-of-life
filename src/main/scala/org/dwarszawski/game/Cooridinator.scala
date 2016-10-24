package org.dwarszawski.game

import akka.actor.{Actor, ActorRef, Props}
import org.dwarszawski.game.CoordinatorCommands.{CurrentState, Start}
import org.dwarszawski.game.GameCellCommands.Spawn
import org.dwarszawski.game.GameCellEvents.Spawned

import scala.collection.immutable.Iterable

class Coordinator extends Actor {

  override def receive: Receive = {
    case Start(size) => {
      val replyTo = sender()
      startGame(size)
      context.become(waiting(List.empty, size * size)(replyTo))
    }
  }

  private def waiting(children: List[ActorRef], size: Int)(replyTo: ActorRef): Receive = {
    case Spawned(x, y) => {
      println(s"actor [$x, $y] is spawned")
      if (children.size == size - 1) {
        context.become(monitoring())
        replyTo ! CoordinatorEvents.Started
      }
      else {
        context.become(waiting(sender() :: children, size)(replyTo))
      }
    }
  }

  private def monitoring(): Receive = {
    case CurrentState => sender() ! cells().size
  }

  private def startGame(size: Int): Unit = {
    for {
      i <- 0 to size
      j <- 0 to size
    } yield spawnCell(i, j)
  }

  private def cells(): Iterable[ActorRef] = context.children

  private def spawnCell(i: Int, j: Int) = {
    val cell = context.actorOf(GameCell.props())
    cell ! Spawn(i, j, Alive) // TODO should be passed
  }
}

object Coordinator {
  def props(): Props = Props(new Coordinator())

}

object CoordinatorCommands {

  case class Start(size: Int)

  case object CurrentState

}

object CoordinatorEvents {

  case object Started

}

