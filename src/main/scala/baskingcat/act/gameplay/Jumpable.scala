package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: State, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  def jump(implicit stage: Stage, ev: A <:< Standing): Jumpable[_ <: Jumping, B]

}
