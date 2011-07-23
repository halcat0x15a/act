package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Bullet extends GameObject

abstract class AbstractBullet[A <: Direction, B <: GameObject with HasDirection[A]](implicit val direction: Manifest[A], val owner: Manifest[B]) extends Bullet with HasDirection[A]

case class Negi[A <: Status, B <: Form, C <: Direction, D <: GameObject with HasDirection[C]](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val form: Manifest[B], direction: Manifest[C], owner: Manifest[D]) extends AbstractBullet[C, D] with Live[A] with Movable[A, B, C] {

  val obstacles = typeList[Cons[Enemy, Cons[Block, Nil]]]

  lazy val name = 'negi

  def update(implicit stage: Stage) = {
    Vector(move/* |> (_.live)*/)
  }

  def movable(bounds: Rectangle): Negi[A, B, C, D] = copy(bounds = bounds)

  def live(velocity: Vector2D, life: Int): Negi[Damaging, B, C, D] = copy[Damaging, B, C, D](velocity = velocity, life = life)

  def apply(implicit stage: Stage) = this

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D = Vector2D(15, 0)

  val Life: Int = 1

  def apply[A <: Direction: Manifest, B <: GameObject with HasDirection[A]: Manifest](owner: B) = {
    val forward = manifest[A] <:< manifest[Forward]
    val x = forward.fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    val v = forward.fold(Velocity, -Velocity)
    new Negi[Moving, Flying, A, B](Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
