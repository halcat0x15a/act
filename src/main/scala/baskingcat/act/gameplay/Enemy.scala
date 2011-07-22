package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: Status, B <: Form, C <: Direction](bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit val status: Manifest[A], val form: Manifest[B], val direction: Manifest[C]) extends GameObject with Live[A] with Walkable[A, B, C] {

  type E = Enemy[_ <: Status, _ <: Form, _ <: Direction]

  val speed: Float = 1f

  lazy val name = 'supu

  def update(implicit stage: Stage) = {
    Vector(stage.objects.any(detect).fold[E](damaged, this))
  }

  def movable(bounds: Rectangle[Float]) = copy(bounds = bounds)

  def walkable[D <: Direction: Manifest](velocity: Vector2D[Float]): Enemy[Walking, B, D] = copy[Walking, B, D](velocity = velocity)

  def apply(implicit stage: Stage): Enemy[A, B, C] = copy(velocity = mzero[Vector2D[Float]])

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Enemy[_, _, _]] && obj.bounds.intersects(bounds)

  def damaged = copy[Damaging, B, C](life = life - 1)

}

object Enemy {

  type Type = Enemy[_ <: Status, _ <: Form, _ <: Direction]

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 1

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Enemy[Idling, Standing, Backward](Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0, 0), Life)
  }

}
