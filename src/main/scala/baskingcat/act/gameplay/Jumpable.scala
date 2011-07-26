package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable extends Movable { obj: GameObject =>

  val jumpPower: Float

  def jump = movable(status = Jumping, velocity = velocity.copy(y = -jumpPower))

}
