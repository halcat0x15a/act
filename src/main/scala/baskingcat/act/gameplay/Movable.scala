package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameplayObject =>

  val velocity: Vector2D[Float]

  def move: Movable[A, B]

  def apply(implicit stage: Stage): Movable[_ <: Status, B]

}
