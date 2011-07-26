package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Shootable[A <: Status, B <: Direction] extends HasStatus[A] with HasDirection[B] { obj: GameObject =>

  val bullet: Bullet

  def shootable[A <: Status: Manifest]: GameObject with Shootable[A, B]

  def shoot: (GameObject with Shootable[_ <: Shooting, B], Bullet) = {
    val s = if (status <:< manifest[Idling])
      shootable[IShooting]
    else if (status <:< manifest[Walking])
      shootable[WShooting]
    else if (status <:< manifest[Jumping])
      shootable[JShooting]
    else
      undefined
    s -> bullet
  }

}
