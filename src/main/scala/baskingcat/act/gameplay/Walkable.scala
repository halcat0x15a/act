package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Walkable[A <: Status, B <: Direction] extends Movable[A, B] { obj: GameObject =>

  val speed: Float

  val acceleration: Float = speed

  def walkable[A <: Status: Manifest, B <: Direction: Manifest](velocity: Vector2D): GameObject with Walkable[A, B]

  def walk[A <: Direction: Manifest]: GameObject with Walkable[Walking, A] = {
    def v(signum: Int) = (velocity.x + acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (direction <:< manifest[Forward]).fold(v(1), v(-1))
    walkable[Walking, A](velocity.copy(x = vx))
  }

}
