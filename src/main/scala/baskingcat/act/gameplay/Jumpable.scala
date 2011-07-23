package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, C <: Direction] extends Movable[A, C] { obj: GameObject =>

  val jumpPower: Float

  def jumpable(velocity: Vector2D): GameObject

  def jump: GameObject = jumpable(velocity.copy(y = -jumpPower))

}
