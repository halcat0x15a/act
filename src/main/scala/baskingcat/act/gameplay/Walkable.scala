package baskingcat.act.gameplay

import baskingcat.act._

trait Walkable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameplayObject =>

  def walk[C <: Direction](direction: C)(implicit stage: Stage): Walkable[_ <: Moving, C]

}
