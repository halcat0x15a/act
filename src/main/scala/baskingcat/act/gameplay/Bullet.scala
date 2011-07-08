package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Bullet[A <: State, B <: Direction](owner: GameplayObject[_ <: State, B], bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit mfa: Manifest[A], mfb: Manifest[B]) extends GameplayObject[A, B] with Movable[A, B] with Live[A, B] {

  lazy val name = 'negi

  def move(implicit ev: <:<[A, baskingcat.act.gameplay.Moving]) = copy[Moving, B](bounds = bounds.copy(location = bounds.location |+| velocity))

  def apply(implicit stage: Stage) = copy[Moving, B](velocity = Vector2D(0f, 0f))

  def damaged(implicit stage: Stage): GameplayObject[A, B] = copy(life = stage.objects.any(obj => obj.bounds.intersects(bounds)).fold(life - 1, life))

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

  val Speed: Float = 10

  def apply[A <: State, B <: Direction](owner: GameplayObject[_ <: State, B])(implicit mfb: Manifest[B]) = {
    val x = (mfb.erasure == Forward.Class).fold(owner.bounds.right, owner.bounds.left - Width)
    val y = owner.bounds.top |+| owner.bounds.size.height / 2 - Height / 2
    new Bullet(owner, Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(Speed, 0), 1)
  }

}
