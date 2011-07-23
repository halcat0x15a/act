package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val bullet: Bullet with HasDirection[B]

  def shootable[A <: Status: Manifest]: GameObject with Shootable[A, B]

  def shoot = shootable[Shooting] -> bullet

}
