package baskingcat.act.gameplay

import baskingcat.act._

trait Walkable[A <: State, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  def walk(implicit stage: Stage): Walkable[_ <: Moving, _ <: Direction]

}
