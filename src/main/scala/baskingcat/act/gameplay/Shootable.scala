package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Shootable[A <: GameObject with Shootable[A]] extends HasStatus[A] with HasDirection[A] { obj: GameObject =>

  val bullet: Bullet

  def shoot = {
    val s = status match {
      case Idling => Shooting
      case Walking => Shooting
      case Jumping => Shooting
    }
    status(s) -> bullet
  }

}
