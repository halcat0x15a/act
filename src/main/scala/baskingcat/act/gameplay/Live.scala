package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live[A <: Status] extends GameObject with HasStatus[A] {

  val obstacles: TypeList

  val life: Int

  val resilience: Vector2D = mzero[Vector2D]

  def live(velocity: Vector2D, life: Int): GameObject

  def detect[A <: GameObject](obj: A)(implicit m: Manifest[A]): Boolean = {
    lazy val b = obj.bounds.intersects(bounds) && obstacles.any(m <:< _)
    obj match {
      case ab: AbstractBullet[_, _] => ab.owner.erasure.isInstance(this)
    }
  }

  def alive(implicit stage: Stage) = stage.filteredObjects.any(detect).fold[GameObject](damaged, this)

  def damaged: GameObject = live(-resilience, life - 1)

  def isDead(implicit stage: Stage): Boolean = life <= 0 || !stage.bounds.intersects(bounds)

}

