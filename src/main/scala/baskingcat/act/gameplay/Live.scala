package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live extends GameObject with HasStatus {

  val obstacles: TypeList

  val life: Int

  def live(status: Status = status, life: Int = life): GameObject with Live

  def damaged = live(life = life - 1)

  def detect[A <: GameObject: Manifest](obj: A): Boolean = {
    lazy val b = obj.bounds.intersects(bounds) && obstacles.any(manifest[A] <:< _)
    obj match {
      case ho: HasOwner[_] => ho.owner.erasure.isInstance(this) && b
      case _ => b
    }
  }

}

