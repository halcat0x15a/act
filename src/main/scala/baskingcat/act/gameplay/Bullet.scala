package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: Status: Manifest, B <: Direction: Manifest](owner: GameObject with HasDirection[B], bounds: Rectangle[Float], velocity: Vector2D[Float]) extends GameplayObject with HasStatus[A] with HasDirection[B] with Movable[A, B] {

  lazy val name = 'negi

  def update(implicit stage: Stage) = Vector(move)

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = this

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D[Float] = Vector2D(10, 0)

  def apply[B <: Direction: Manifest](owner: GameObject with HasDirection[B]) = {
    val x = (manifest[B] <:< manifest[Forward]).fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    new Bullet[Walking with Flying, B](owner, Rectangle(Point(x, y), Dimension(Width, Height)), Velocity)
  }

}
