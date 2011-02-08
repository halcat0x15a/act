package baskingcat.act.game

import baskingcat.act._
import collection._

@cloneable abstract class GameObject(var vx: Float = 0, var vy: Float = 0) extends ACTObject with Product {

  val id: String

  val fixed: Boolean

  def clone[T <: GameObject] = super.clone.asInstanceOf[T]

  def intersects(obj: GameObject) = left <= obj.right && right >= obj.left && top >= obj.bottom && bottom <= obj.top

  def findGround(landableSet: Set[Block]) = landableSet find (ground => bottom <= ground.top && top > ground.top && left < ground.right && right > ground.left)

}
