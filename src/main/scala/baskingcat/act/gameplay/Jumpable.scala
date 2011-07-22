package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, B <: Form, C <: Direction] extends Movable[A, B, C] { obj: GameObject =>

  def jump: Jumpable[_ <: Jumping, _ <: Form, C]

}
