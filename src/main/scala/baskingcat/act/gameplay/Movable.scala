package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: Status, B <: Form] extends HasStatus[A] with HasForm[B] {

  val velocity: Vector2D[Float]

  def move: Movable[A, B]

  def apply(implicit stage: Stage): Movable[_ <: Status, _ <: Form]

}
