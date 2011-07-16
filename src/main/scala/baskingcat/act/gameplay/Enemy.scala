package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: Status: Manifest, B <: Direction: Manifest](bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int) extends GameplayObject with HasStatus[A] with HasDirection[B] with Live[A] with Movable[A, B] with Walkable[A, B] {

  lazy val name = 'supu

  def update(implicit stage: Stage) = Vector(stage.objects.any(detect).fold(damaged, this))

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[C <: Direction: Manifest] = copy[Walking with A, C](velocity = mzero[Vector2D[Float]])

  def apply(implicit stage: Stage) = copy(velocity = mzero[Vector2D[Float]])

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Enemy[_, _]] && obj.bounds.intersects(bounds)

  def damaged = {
    copy[Damaging with A, B](life = life - 1)
  }

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 5

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Enemy[Normal with Standing, Backward](Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0, 0), Life)
  }

}
