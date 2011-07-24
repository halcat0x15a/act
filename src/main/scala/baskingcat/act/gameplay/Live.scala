package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live[A <: Status] extends GameObject with HasStatus[A] {

  val obstacles: TypeList

  val life: Int

  def live[A <: Status: Manifest](life: Int): GameObject with Live[A]

  def damaged: GameObject = live(life - 1)

  def detect[A <: GameObject: Manifest](obj: A): Boolean = {
    lazy val b = obj.bounds.intersects(bounds) && obstacles.any(manifest[A] <:< _)
    obj match {
      case ho: HasOwner[_] => ho.owner.erasure.isInstance(this) && b
      case _ => b
    }
  }

}

