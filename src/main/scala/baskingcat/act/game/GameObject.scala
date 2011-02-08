package baskingcat.act.game

import baskingcat.act._
import collection._

abstract class GameObject extends ACTObject {

  val id: Int

  val vx: Float

  val vy: Float

  def intersects(obj: GameObject) = left <= obj.right && right >= obj.left && top >= obj.bottom && bottom <= obj.top

  def findGround(landableSet: Set[Landable]) = {
    landableSet find (ground => bottom <= ground.top && top > ground.top && left < ground.right && right > ground.left)
  }

}
