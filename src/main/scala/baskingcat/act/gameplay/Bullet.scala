package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: Status, B <: Form, C <: Direction](owner: GameObject with HasDirection[C], bounds: Rectangle[Float], velocity: Vector2D[Float])(implicit status: Manifest[A], form: Manifest[B], direction: Manifest[C]) extends GameplayObject with HasStatus[A] with HasForm[B] with HasDirection[C] with Movable[A, B] {

  lazy val name = 'negi

  def update(implicit stage: Stage) = Vector(move)

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = this

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D[Float] = Vector2D(10, 0)

  def apply[A <: Direction: Manifest](owner: GameObject with HasDirection[A]) = {
    val x = (manifest[A] <:< manifest[Forward]).fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    new Bullet[Walking, Flying, A](owner, Rectangle(Point(x, y), Dimension(Width, Height)), Velocity)
  }

}
