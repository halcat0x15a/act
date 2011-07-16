package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] {

  def shoot: (Shootable[_ <: Shooting, B], Bullet[_ <: Status, _ <: Form, B])

}
