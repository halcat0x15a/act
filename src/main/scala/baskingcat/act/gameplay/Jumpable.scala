package baskingcat.act.gameplay

import baskingcat.act._

trait Jumpable[A <: State, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  def jump(implicit ev: A <:< Standing, stage: Stage): Jumpable[_ <: Jumping, B]

}
