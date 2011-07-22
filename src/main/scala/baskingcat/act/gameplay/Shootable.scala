package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  type BulletType = Bullet[_ <: Status, _ <: Form, B]

  val bullet: BulletType

  def shootable: GameObject

  def shoot: (GameObject, BulletType) = shootable -> bullet

}
