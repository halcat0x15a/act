package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: State, B <: Direction](owner: GameObject with HasDirection[B], state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float]) extends GameObject with HasState[A] with HasDirection[B] with Movable[A, B] {

  lazy val name = 'negi

  def update = this

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = copy(velocity = Vector2D(0f, 0f))

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Speed: Float = 10

  def apply[A <: State, B <: Direction](owner: GameObject with HasDirection[B]) = {
    val x = owner.direction.isInstanceOf[Forward].fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    new Bullet(owner, Normal(), owner.direction, Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(Speed, 0))
  }

}
