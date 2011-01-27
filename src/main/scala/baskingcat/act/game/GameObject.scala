package baskingcat.act.game

import baskingcat.act._
import collection._

abstract class GameObject extends ACTObject {

  val id: Int

  val vx: Float

  val vy: Float

  val life: Float

  val invincible: Boolean

  def damaged(damagable: Set[Damagable]) = {
    life - (damagable find (hit) match {
      case Some(damagable) => damagable.power
      case None => 0f
    })
  }

  def hit(obj: GameObject) = {
    left <= obj.right && right >= obj.left && top >= obj.bottom && bottom <= obj.top
  }

  private var groundMap = mutable.Map.empty[Set[Landable], Option[Landable]]

  def ground(landableSet: Set[Landable]) = {
    if (!groundMap.contains(landableSet)) {
      groundMap(landableSet) = landableSet find (ground => bottom <= ground.top && top > ground.top && left < ground.right && right > ground.left)
    }
    groundMap(landableSet)
  }

}
