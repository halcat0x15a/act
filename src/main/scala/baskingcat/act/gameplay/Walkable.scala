package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Walkable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  val speed: Float

  val acceleration: Float = speed

  def walk[B <: Direction: Manifest] = {
    def v(signum: Int) = (velocity.x + acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (manifest[B] <:< manifest[Forward]).fold(v(1), v(-1))
    def cp[A <: Status: Manifest] = movable[A, B](velocity = velocity.copy(x = vx))
    if (status <:< manifest[Jumping])
      cp[JWalking]
    else
      cp[Walking]
  }

}
