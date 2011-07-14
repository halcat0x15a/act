package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameplayObject =>

  def shoot: (Shootable[Shooting, B], Bullet[_ <: Status, B])

}
