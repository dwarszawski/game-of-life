package org.dwarszawski.game

import akka.actor.{Actor, Props}
import org.dwarszawski.game.GameCellCommands._
import org.dwarszawski.game.GameCellEvents.{CellState, Spawned}
import akka.pattern.{ask, pipe}
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.Future

trait Cell extends Actor {}

class GameCell() extends Cell {
  implicit val timeout = Timeout(2 seconds)

  implicit def executorContext = context.dispatcher

  override def receive: Receive = {
    case Spawn(xCoordinate, yCoordinate, state: State) => {
      sender() ! Spawned(xCoordinate, yCoordinate)
      context.become(introducing(List.empty, state))
    }
  }

  private def introducing(neighbours: List[Neighbour], state: State): Receive = {

    case Introduce(neighbour) => {
      context.become(introducing(neighbour :: neighbours, state))
    }
    case Enough => context.become(running(neighbours, state))
  }

  private def running(neighbours: List[Neighbour], state: State): Receive = {
    case GetNextState => {
      val neighbourStates = neighbours.map { case n: Neighbour => (n ? GetCurrentState).mapTo[CellState] }
      Future.sequence(neighbourStates).map(getState).pipeTo(sender())
    }
    case GetCurrentState => sender() ! CellState(state)
    case UpdateState(state: State) => context.become(running(neighbours, state))
  }

  private def getState(states: List[CellState]): CellState = {
    val alivedCells: Int = states.map(_.state).count(_.isAlived)
    println(s"Alived cells is $alivedCells")
    if (alivedCells == 2 || alivedCells == 3) CellState(Alive) else CellState(Dead)
  }
}

object GameCell {
  def props(): Props = Props(new GameCell())
}

object GameCellCommands {

  case class Spawn(xCoordinate: Int, yCoordinate: Int, state: State)

  case class Introduce(neighbour: Neighbour)

  case object Enough

  case object GetNextState

  case object GetCurrentState

  case class UpdateState(state: State)

}

object GameCellEvents {

  case class Spawned(xCoordinate: Int, yCoordinate: Int)

  case class CellState(state: State)

}