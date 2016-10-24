package org.dwarszawski.game

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, FlatSpecLike, GivenWhenThen, Matchers}

class CoordinatorTest extends TestKit(ActorSystem("Cell")) with ImplicitSender with GivenWhenThen with FlatSpecLike with Matchers with BeforeAndAfterAll {
  it should "should create coordinator and spawn cell actors" in {
    Given("new game")
    val coordinator = system.actorOf(Coordinator.props)

    When("start command called")
    coordinator ! CoordinatorCommands.Start(2)

    Then("cells are spawned")
    expectMsg(CoordinatorEvents.Started)

  }
}
