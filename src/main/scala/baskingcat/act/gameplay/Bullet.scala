package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: Status, B <: Direction](owner: GameObject with HasDirection[B], status: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float]) extends GameplayObject with HasStatus[A] with HasDirection[B] with Movable[A, B] {

  lazy val name = 'negi

  def update(implicit stage: Stage) = status match {
    case m: Moving => Vector(copy(status = m).move)
  }

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = this

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D[Float] = Vector2D(10, 0)

  def apply[B <: Direction](owner: GameObject with HasDirection[B]) = {
    val x = owner.direction.isInstanceOf[Forward].fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    new Bullet(owner, new Walking with Flying, owner.direction, Rectangle(Point(x, y), Dimension(Width, Height)), Velocity)
  }

}
