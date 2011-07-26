package baskingcat.act.gameplay

import baskingcat.act._

trait Movable extends HasStatus with HasDirection { obj: GameObject =>

  val velocity: Vector2D

  def movable(status: Status = status, direction: Direction = direction, bounds: Rectangle = bounds, velocity: Vector2D = velocity): GameObject with Movable

  def move = movable(bounds = bounds.copy(location = bounds.location + velocity))

}
