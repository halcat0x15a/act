package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Shootable extends HasStatus with HasDirection { obj: GameObject =>

  val bullet: Bullet

  def shootable(status: Status = status): GameObject with Shootable

  def shoot = {
    val s = status match {
      case Idling => Shooting
      case Walking => Shooting
      case Jumping => Shooting
    }
    shootable(s) -> bullet
  }

}
