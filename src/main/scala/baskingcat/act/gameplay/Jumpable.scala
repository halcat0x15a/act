package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: GameObject with Jumpable[A]] extends Movable[A] { obj: GameObject =>

  val jumpPower: Float

  def jump = velocity(velocity.copy(y = -jumpPower)).status(Jumping)

}
