package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: Status, B <: Form] extends Movable[A, B] {

  def jump: Jumpable[_ <: Jumping, _ <: Form]

}
