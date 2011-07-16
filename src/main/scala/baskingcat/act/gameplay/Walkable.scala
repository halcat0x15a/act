package baskingcat.act.gameplay

import baskingcat.act._

trait Walkable[A <: Status, B <: Form, C <: Direction] extends Movable[A, B] {

  def walk[D <: Direction: Manifest]: Walkable[_ <: Status, B, D]

}
