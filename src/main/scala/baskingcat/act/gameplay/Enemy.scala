package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Enemy[A <: State, B <: Direction](state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int) extends GameObject with HasState[A] with HasDirection[B] with Live[A] with Movable[A, B] with Walkable[A, B] {

  lazy val name = 'supu

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk(implicit stage: Stage) = copy(state = Walking(), velocity = Vector2D(0f, 0f))

  def apply(implicit stage: Stage) = copy(velocity = Vector2D(0f, 0f))

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Enemy[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = copy(state = Damaging(), life = stage.objects.any(detect).fold(life - 1, life))

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 5

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Enemy(Normal(), Backward(), Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0, 0), Life)
  }

}
