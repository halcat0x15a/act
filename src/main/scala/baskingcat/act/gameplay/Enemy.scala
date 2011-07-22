package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Enemy extends GameObject

case class Supu[A <: Status, B <: Form, C <: Direction](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val form: Manifest[B], val direction: Manifest[C]) extends Enemy with Live[A] with Walkable[A, B, C] {

  val obstacles = typeList[Cons[Bullet, Nil]]

  val speed: Float = 1f

  lazy val name = 'supu

  def update(implicit stage: Stage) = {
    Vector(stage.objects.any(detect).fold[GameObject](damaged, this))
  }

  def movable(bounds: Rectangle) = copy(bounds = bounds)

  def walkable[D <: Direction: Manifest](velocity: Vector2D): Supu[Walking, B, D] = copy[Walking, B, D](velocity = velocity)

  def live(velocity: Vector2D, life: Int): Supu[Damaging, B, C] = copy[Damaging, B, C](velocity = velocity, life = life)

  def apply(implicit stage: Stage): Supu[A, B, C] = copy(velocity = mzero[Vector2D])

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 1

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Supu[Idling, Standing, Backward](Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}
