package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: GameObject with Movable[A]] extends HasStatus[A] with HasDirection[A] { obj: GameObject =>

  val velocity: Vector2D

  def bounds(bounds: Rectangle): A

  def velocity(velocity: Vector2D): A

  def move = bounds(bounds.copy(location = bounds.location + velocity))

}
