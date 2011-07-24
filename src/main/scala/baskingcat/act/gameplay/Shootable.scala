package baskingcat.act.gameplay

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val bullet: Bullet with HasDirection[B]

  def shootable[A <: Status: Manifest]: GameObject with Shootable[A, B]

  def shoot = {
    val s = status match {
      case Idling.Manifest => shootable[Shooting[Idling]]
      case Walking.Manifest => shootable[Shooting[Walking]]
      case Jumping.Manifest => shootable[Shooting[Jumping]]
    }
    s -> bullet
  }

}
