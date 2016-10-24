package org.dwarszawski.game

sealed trait State{
  val isAlived: Boolean
}

case object Alive extends State {
  override val isAlived = true
}

case object Dead extends State {
  override val isAlived = false
}

sealed trait Mode

object Running extends Mode

object Configuring extends Mode