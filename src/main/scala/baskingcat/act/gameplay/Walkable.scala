package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Walkable[A <: GameObject with Walkable[A]] extends Movable[A] { obj: GameObject =>

  val speed: Float

  val acceleration: Float = speed

  def walk(direction: Direction) = {
    def v(signum: Int) = (velocity.x + acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (direction == Forward).fold(v(1), v(-1))
    val s = (status == Jumping).fold(Jumping, Walking)
    velocity(velocity.copy(x = vx)).status(s)
  }

}
