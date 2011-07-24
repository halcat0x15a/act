package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Bullet extends GameObject

trait HasOwner[A <: GameObject] {

  implicit val owner: Manifest[A]

}

case class Negi[A <: Status, B <: Direction, C <: GameObject](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val direction: Manifest[B], val owner: Manifest[C]) extends Bullet with HasOwner[C] with Live[A] with Movable[A, B] {

  val obstacles = typeList[Cons[Enemy, Cons[Block, Nil]]]

  lazy val name = 'negi

  def movable[A <: Status: Manifest, B <: Direction: Manifest](bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy[A, B, C](bounds = bounds, velocity = velocity)

  def live[A <: Status: Manifest](life: Int) = copy[A, B, C](life = life)

}

object Negi {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D = Vector2D(15, 0)

  val Life: Int = 1

  def apply[A <: Direction: Manifest, B <: GameObject: Manifest](owner: B) = {
    val forward = manifest[A] <:< manifest[Forward]
    val x = forward.fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top + owner.bounds.size.height / 2 - Height / 2
    val v = forward.fold(Velocity, -Velocity)
    new Negi[Moving, A, B](Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
