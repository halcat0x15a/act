package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: State, B <: Direction](owner: GameplayObject[_ <: State, B], bounds: Rectangle, velocity: Vector2f, life: Int)(implicit mfb: Manifest[B]) extends GameplayObject[A, B] with Movable[A, B] with Live[A, B] {

  lazy val name = 'negi

  def move = copy(bounds = bounds.copy(location = bounds.location + velocity))

  def apply(implicit stage: Stage) = copy(velocity = new Vector2f)

  def damaged(implicit stage: Stage): GameplayObject[A, B] = copy(life = stage.objects.any(obj => obj.bounds.intersects(bounds)).fold(life - 1, life))

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Speed: Float = 10

  def apply[A <: State, B <: Direction](owner: GameplayObject[_ <: State, B])(implicit mfb: Manifest[B]) = {
    val x = (mfb.erasure == Forward.Class).fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top + owner.bounds.size.height / 2 - Height / 2
    new Bullet(owner, Rectangle(Vector2f(x, y), Dimension(Width, Height)), Vector2f(Speed, 0), 1)
  }

}
