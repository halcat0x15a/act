package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  val jumpPower: Float

  def jumpable[A <: Status: Manifest](velocity: Vector2D): GameObject with Jumpable[A, B]

  def jump = jumpable(velocity.copy(y = -jumpPower))

}
