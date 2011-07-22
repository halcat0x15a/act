package baskingcat.act.gameplay

import baskingcat.act._

trait Walkable[A <: Status, B <: Form, C <: Direction] extends Movable[A, B, C] { obj: GameObject =>

  val speed: Float

  def walk[D <: Direction: Manifest]: Walkable[_ <: Status, B, D]

}
