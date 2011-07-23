package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Walkable[A <: Status, C <: Direction] extends Movable[A, C] { obj: GameObject =>

  val speed: Float

  val acceleration: Float = speed

  def walkable[D <: Direction: Manifest](velocity: Vector2D): GameObject

  def walk[D <: Direction: Manifest]: GameObject = {
    def v(signum: Int) = (velocity.x + acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (direction <:< manifest[Forward]).fold(v(1), v(-1))
    walkable[D](velocity.copy(x = vx))
  }

}
