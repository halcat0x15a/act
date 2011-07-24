package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Walkable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  val speed: Float

  val acceleration: Float = speed

  def walk[A <: Direction: Manifest] = {
    def v(signum: Int) = (velocity.x + acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (direction <:< manifest[Forward]).fold(v(1), v(-1))
    movable[Walking, A](velocity = velocity.copy(x = vx))
  }

}
