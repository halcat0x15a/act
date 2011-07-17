package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: Status, B <: Form, C <: Direction](bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit status: Manifest[A], form: Manifest[B], direction: Manifest[C]) extends GameplayObject with Live[A] with Walkable[A, B, C] {

  type E = Enemy[_ <: Status, _ <: Form, _ <: Direction]

  val speed: Float = 1f

  lazy val name = 'supu

  def update(implicit stage: Stage) = {
    Vector(stage.objects.any(detect).fold[E](damaged, this))
  }

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[D <: Direction: Manifest]: Enemy[_ <: Walking, B, D] = copy[Walking, B, D](velocity = mzero[Vector2D[Float]])

  def apply(implicit stage: Stage): Enemy[A, B, C] = copy(velocity = mzero[Vector2D[Float]])

  def detect(obj: GameplayObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Enemy[_, _, _]] && obj.bounds.intersects(bounds)

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
