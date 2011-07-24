package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  val jumpPower: Float

  def jump = movable[Jumping, B](velocity = velocity.copy(y = -jumpPower))

}
