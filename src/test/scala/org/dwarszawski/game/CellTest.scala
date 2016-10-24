package org.dwarszawski.game

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.dwarszawski.game.GameCellCommands._
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, GivenWhenThen, Matchers}

import scala.concurrent.duration._

class CellTest extends TestKit(ActorSystem("Cell")) with ImplicitSender with GivenWhenThen with FlatSpecLike with Matchers with BeforeAndAfterAll {

  it should "spawn cell, get current state of cell and properly move to next state" in {
    Given("game cells")
    val cell = system.actorOf(GameCell.props())
    val neighbour = system.actorOf(GameCell.props())

    When("game cells gets start signal")
    cell ! GameCellCommands.Spawn(1, 1, Alive)

    Then("cells are spawned")
    expectMsgAllOf(2 seconds, GameCellEvents.Spawned(1, 1))

    And("coordinator runs cell")
    cell ! Enough

    And("coordinator ask for state")
    cell ! GetCurrentState

    Then("current state is alive")
    expectMsg(GameCellEvents.CellState(Alive))

    When("coordinator ask for next state")
    cell ! GetNextState

    Then("next state should be dead")
    expectMsg(GameCellEvents.CellState(Dead))

    When("update cell state")
    cell ! UpdateState(Dead)

    Then("current state should be dead")
    cell ! GetCurrentState
    expectMsg(GameCellEvents.CellState(Dead))
  }

  it should "make cell ALIVE because population grows" in {
    Given("game cells")
    val cell = system.actorOf(GameCell.props())
    val aliveNeighbour = system.actorOf(GameCell.props())
    val deadNeighbour = system.actorOf(GameCell.props())

    When("game cells gets start signal")
    cell ! GameCellCommands.Spawn(1, 1, Alive)
    aliveNeighbour ! GameCellCommands.Spawn(1, 2, Alive)
    deadNeighbour ! GameCellCommands.Spawn(1, 3, Dead)

    Then("cells are spawned")
    expectMsgAllOf(2 seconds, GameCellEvents.Spawned(1, 1), GameCellEvents.Spawned(1, 2), GameCellEvents.Spawned(1, 3))

    When("neighbours are introduced")
    (0 until 3).foreach { i => cell ! Introduce(aliveNeighbour) }
    (0 until 5).foreach { i => cell ! Introduce(deadNeighbour) }

    And("coordinator runs cell")
    cell ! Enough
    aliveNeighbour ! Enough
    deadNeighbour ! Enough

    And("coordinator ask for state")
    cell ! GetCurrentState

    Then("current state is alive")
    expectMsg(GameCellEvents.CellState(Alive))

    When("coordinator ask for next state")
    cell ! GetNextState

    Then("next state should be dead")
    expectMsg(GameCellEvents.CellState(Alive))
  }


  it should "make cell DEAD because starving" in {
    Given("game cells")
    val cell = system.actorOf(GameCell.props())
    val aliveNeighbour = system.actorOf(GameCell.props())
    val deadNeighbour = system.actorOf(GameCell.props())

    When("game cells gets start signal")
    cell ! GameCellCommands.Spawn(1, 1, Alive)
    aliveNeighbour ! GameCellCommands.Spawn(1, 2, Alive)
    deadNeighbour ! GameCellCommands.Spawn(1, 3, Dead)

    Then("cells are spawned")
    expectMsgAllOf(2 seconds, GameCellEvents.Spawned(1, 1), GameCellEvents.Spawned(1, 2), GameCellEvents.Spawned(1, 3))

    When("neighbours are introduced")
    (0 until 1).foreach { i => cell ! Introduce(aliveNeighbour) }
    (0 until 7).foreach { i => cell ! Introduce(deadNeighbour) }

    And("coordinator runs cell")
    cell ! Enough
    aliveNeighbour ! Enough
    deadNeighbour ! Enough

    And("coordinator ask for state")
    cell ! GetCurrentState

    Then("current state is alive")
    expectMsg(GameCellEvents.CellState(Alive))

    When("coordinator ask for next state")
    cell ! GetNextState

    Then("next state should be dead")
    expectMsg(GameCellEvents.CellState(Dead))
  }


  it should "make cell DEAD because overpopulation" in {
    Given("game cells")
    val cell = system.actorOf(GameCell.props())
    val aliveNeighbour = system.actorOf(GameCell.props())

    When("game cells gets start signal")
    cell ! GameCellCommands.Spawn(1, 1, Alive)
    aliveNeighbour ! GameCellCommands.Spawn(1, 2, Alive)

    Then("cells are spawned")
    expectMsgAllOf(2 seconds, GameCellEvents.Spawned(1, 1), GameCellEvents.Spawned(1, 2), GameCellEvents.Spawned(1, 3))

    When("neighbours are introduced")
    (0 until 8).foreach { i => cell ! Introduce(aliveNeighbour) }

    And("coordinator runs cell")
    cell ! Enough
    aliveNeighbour ! Enough

    And("coordinator ask for state")
    cell ! GetCurrentState

    Then("current state is alive")
    expectMsg(GameCellEvents.CellState(Alive))

    When("coordinator ask for next state")
    cell ! GetNextState

    Then("next state should be dead")
    expectMsg(GameCellEvents.CellState(Dead))
  }
}
