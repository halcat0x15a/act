package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: Status, B <: Direction](state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int) extends GameplayObject with HasStatus[A] with HasDirection[B] with Live[A] with Movable[A, B] with Walkable[A, B] {

  lazy val name = 'supu

  def update(implicit stage: Stage) = this

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[C <: Direction](direction: C)(implicit stage: Stage) = {
    val s = state match {
      case _: Standing => new Walking with Standing
      case _: Flying => new Walking with Flying
    }
    copy(state = s, direction = direction, velocity = mzero[Vector2D[Float]])
  }

  def apply(implicit stage: Stage) = copy(velocity = mzero[Vector2D[Float]])

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Enemy[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = {
    val s = state match {
      case _: Standing => new Damaging with Standing
      case _: Flying => new Damaging with Flying
    }
    copy(state = s, life = stage.objects.any(detect).fold(life - 1, life))
  }

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 5

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Enemy(new Normal with Standing, new Backward, Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0, 0), Life)
  }

}
