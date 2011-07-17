package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: Status, B <: Form, C <: Direction](owner: GameObject with HasDirection[C], bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit status: Manifest[A], form: Manifest[B], direction: Manifest[C]) extends GameplayObject with Live[A] with Movable[A, B] {

  lazy val name = 'negi

  def update(implicit stage: Stage) = {
    Vector(move |> (_.live))
  }

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = this

  def detect(obj: GameplayObject): Boolean = obj.bounds.intersects(bounds) && cond(obj) {
    case _: Block => true
    case _: Enemy.Type => true
  }

  def damaged: Bullet[_ <: Damaging, B, C] = copy[Damaging, B, C](life = life - 1)

}

object Bullet {

  type Type = Bullet[_ <: Status, _ <: Form, _ <: Direction]

  val Width: Float = 16

  val Height: Float = 16

  val Velocity: Vector2D[Float] = Vector2D(15, 0)

  val Life: Int = 1

  def apply[A <: Direction: Manifest](owner: GameObject with HasDirection[A]) = {
    val forward = manifest[A] <:< manifest[Forward]
    val x = forward.fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    val v = forward.fold(Velocity, -Velocity)
    new Bullet[Walking, Flying, A](owner, Rectangle(Point(x, y), Dimension(Width, Height)), v, Life)
  }

}
