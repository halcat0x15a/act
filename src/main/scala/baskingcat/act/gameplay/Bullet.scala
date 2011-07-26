package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Bullet extends GameObject

trait HasOwner[A <: GameObject] {

  implicit val owner: Manifest[A]

}

case class Negi[A <: GameObject with HasDirection](status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val owner: Manifest[A]) extends Bullet with HasOwner[A] with Live with Movable {

  val obstacles = typeList[Cons[Enemy, Cons[Block, Nil]]]

  lazy val name = 'negi

  def movable(status: Status = status, dierction: Direction, bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy(status = status, direction = direction, bounds = bounds, velocity = velocity)

  def live(status: Status = status, life: Int = life) = copy(status = status, life = life)

}

object Negi {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D = Vector2D(15, 0)

  val Life: Int = 1

  def apply[A <: GameObject with HasDirection: Manifest](owner: A) = {
    val forward = owner.direction == Forward
    val x = forward.fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top + owner.bounds.size.height / 2 - Height / 2
    val v = forward.fold(Velocity, -Velocity)
    new Negi[A](Walking, Forward, Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
