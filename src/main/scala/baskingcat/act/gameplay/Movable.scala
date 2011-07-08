package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: State, B <: Direction] extends HasState[A] with HasDirection[B] { obj: GameObject =>

  val velocity: Vector2D[Float]

  def move(implicit ev: A <:< Moving): Movable[A, B]

  def apply(implicit stage: Stage): Movable[_ <: State, B]

}
