package baskingcat.act.gameplay

import baskingcat.act._

trait Walkable[A <: State, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  def walk[C <: Direction](direction: C)(implicit stage: Stage): Walkable[_ <: Moving, C]

}
