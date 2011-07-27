package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live[A <: GameObject with Live[A]] extends HasStatus[A] { obj: GameObject =>

  val obstacles: TypeList

  val life: Int

  def life(life: Int): A

  def damaged = life(life - 1)

  def detect[A <: GameObject: Manifest](obj: A): Boolean = {
    lazy val b = obj.bounds.intersects(bounds) && obstacles.any(manifest[A] <:< _)
    obj match {
      case ho: HasOwner[_] => ho.owner.erasure.isInstance(this) && b
      case _ => b
    }
  }

}

