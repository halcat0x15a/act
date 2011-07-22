package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, B <: Form, C <: Direction] extends Movable[A, B, C] { obj: GameObject =>

  val jumpPower: Float

  def jumpable(velocity: Vector2D): GameObject

  def jump: GameObject = jumpable(velocity.copy(y = -jumpPower))

}
