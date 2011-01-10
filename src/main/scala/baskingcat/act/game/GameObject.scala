package baskingcat.act.game

import baskingcat.act._

abstract class GameObject extends ACTObject {

  val id: Int

  val vx: Float = 0

  val vy: Float = 0

  val direction: Direction.Value

  val life: Float

  val invincible: Boolean

  def damaged(damagable: Set[Damagable]) = {
    life - (damagable find (hit) match {
      case Some(damagable) => damagable.power
      case None => 0f
    })
  }

  lazy val turn = {
    direction match {
      case Direction.Right => Direction.Left
      case Direction.Left => Direction.Right
    }
  }

  def hit(obj: GameObject) = {
    left <= obj.right && right >= obj.left && top >= obj.bottom && bottom <= obj.top
  }

  def ground(landableSet: Set[Landable]) = landableSet find (ground => bottom <= ground.top && top > ground.top && left < ground.right && right > ground.left) match {
    case Some(landable) => landable
    case None => null
  }

}
