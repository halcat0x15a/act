package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Bullet extends GameObject

trait HasOwner[A <: GameObject] {

  implicit val owner: Manifest[A]

}

case class Negi[A <: GameObject with HasDirection[A]](status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val owner: Manifest[A]) extends Bullet with HasOwner[A] with Live[Negi[A]] with Movable[Negi[A]] {

  val obstacles = typeList[Cons[Enemy, Cons[Block, Nil]]]

  lazy val name = 'negi

  def status(status: Status): Negi[A] = copy(status = status)

  def direction(dierction: Direction): Negi[A] = copy(direction = direction)

  def life(life: Int): Negi[A] = copy(life = life)

  def bounds(bounds: Rectangle): Negi[A] = copy(bounds = bounds)

  def velocity(velocity: Vector2D): Negi[A] = copy(velocity = velocity)

}

object Negi {

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D = Vector2D(15, 0)

  val Life: Int = 1

  def apply[A <: GameObject with HasDirection[A]: Manifest](owner: A) = {
    val forward = owner.direction == Forward
    val x = forward.fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top + owner.bounds.size.height / 2 - Height / 2
    val v = forward.fold(Velocity, -Velocity)
    new Negi[A](Walking, Forward, Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
