package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: State, B <: Direction](bounds: Rectangle, velocity: Vector2[Float], life: Int) extends GameplayObject[A, B] with Live[A, B] with Movable[A, B] with Walkable[A, B] {

  lazy val name = 'supu

  def move = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk(implicit stage: Stage) = copy(velocity = Vector2(0f, 0f))

  def apply(implicit stage: Stage) = copy(velocity = Vector2(0f, 0f))

  def detect(obj: GameplayObject[_, _]) = !obj.isInstanceOf[Block[_, _]] && !obj.isInstanceOf[Enemy[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = copy(life = stage.objects.any(detect).fold(life - 1, life))

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 5

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Enemy[Normal, Backward](Rectangle(Vector2(x, y), Dimension(Width, Height)), Vector2(0, 0), Life)
  }

}
