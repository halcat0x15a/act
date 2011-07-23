package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val velocity: Vector2D

  def movable(bounds: Rectangle): GameObject with Movable[A, B]

  def move: GameObject with Movable[A, B] = movable(bounds.copy(location = bounds.location + velocity))

}
