package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val velocity: Vector2D

  def movable[A <: Status: Manifest, B <: Direction: Manifest](bounds: Rectangle = bounds, velocity: Vector2D = velocity): GameObject with Movable[A, B]

  def move = movable[A, B](bounds = bounds.copy(location = bounds.location + velocity))

}
