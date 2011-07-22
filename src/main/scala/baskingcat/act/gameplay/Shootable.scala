package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val bullet: Bullet

  def shootable: GameObject

  def shoot: (GameObject, Bullet) = shootable -> bullet

}
