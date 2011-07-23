package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Bullet extends GameObject

trait HasOwner[A <: Direction, B <: GameObject with HasDirection[A]] extends HasDirection[A] {

  val owner: Manifest[B]

}

case class Negi[A <: Status, B <: Direction, C <: GameObject with HasDirection[B]](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val direction: Manifest[B], val owner: Manifest[C]) extends Bullet with HasOwner[B, C] with Live[A] with Movable[A, B] {

  val obstacles = typeList[Cons[Enemy, Cons[Block, Nil]]]

  lazy val name = 'negi

  def movable(bounds: Rectangle) = copy(bounds = bounds)

  def live[A <: Status: Manifest](life: Int) = copy[A, B, C](life = life)

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
    new Negi[Moving, A, B](Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
