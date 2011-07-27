package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Update[A <: GameObject] {

  val properties: GameProperties

  val stage: Stage

  def update(obj: A): GameObjects

  def grounds(obj: GameObject) = stage.blocks.filter(block => block.bounds.top <= obj.bounds.bottom && block.bounds.bottom >= obj.bounds.bottom && obj.bounds.intersectsh(block.bounds))

  def ceilings(obj: GameObject) = stage.blocks.filter(block => block.bounds.bottom >= obj.bounds.top && block.bounds.top <= obj.bounds.top && obj.bounds.intersectsh(block.bounds))

  def rwalls(obj: GameObject) = stage.blocks.filter(block => block.bounds.left <= obj.bounds.right && block.bounds.right >= obj.bounds.right && obj.bounds.intersectsv(block.bounds))

  def lwalls(obj: GameObject) = stage.blocks.filter(block => block.bounds.right >= obj.bounds.left && block.bounds.left <= obj.bounds.left && obj.bounds.intersectsv(block.bounds))

  def apply[A <: GameObject with Movable[A]](obj: A): A = {
    val vx = if (lwalls(obj).nonEmpty || rwalls(obj).nonEmpty)
      mzero[Float]
    else if (obj.velocity.x > 0)
      obj.velocity.x - stage.friction
    else if (obj.velocity.x < 0)
      obj.velocity.x |+| stage.friction
    else
      obj.velocity.x
    val vy = (grounds(obj).nonEmpty || (ceilings(obj).nonEmpty && obj.velocity.y < 0)) ? 0f | (obj.velocity.y |+| stage.gravity)
    obj.velocity(Vector2D(vx, vy))
  }

  def fix[A <: GameObject with Movable[A]](obj: A): A = {
    (grounds(obj), ceilings(obj), lwalls(obj), rwalls(obj)) |> {
      case (grounds, ceilings, lwalls, rwalls) => {
        lazy val groundst = grounds.map(_.bounds.top).min
        lazy val ceilingsb = ceilings.map(_.bounds.bottom).max
        lazy val (vmargin, y) = if (grounds.nonEmpty)
          (obj.bounds.bottom - groundst) -> (groundst - obj.bounds.size.height)
        else if (ceilings.nonEmpty)
          (ceilingsb - obj.bounds.top) -> ceilingsb
        else
          mzero[Float] -> obj.bounds.location.y
        lazy val rwallsl = rwalls.map(_.bounds.left).min
        lazy val lwallsr = lwalls.map(_.bounds.right).max
        lazy val (hmargin, x) = if (rwalls.nonEmpty)
          (obj.bounds.right - rwallsl) -> (rwallsl - obj.bounds.size.width)
        else if (lwalls.nonEmpty)
          (lwallsr - obj.bounds.left) -> lwallsr
        else
          mzero[Float] -> obj.bounds.location.x
        lazy val location = if (vmargin < hmargin)
          obj.bounds.location.copy(y = y)
        else if (hmargin < vmargin)
          obj.bounds.location.copy(x = x)
        else
          Point(x, y)
        if ((hmargin /== 0) && (vmargin /== 0))
          fix(obj.bounds(obj.bounds.copy(location = location)))
        else
          obj
      }
    }
  }

  def movef[A <: GameObject with Movable[A]](obj: A): A = fix(apply(obj.move))

  def isDead[A <: GameObject with Live[A]](obj: A): Boolean = obj.life <= 0 || !stage.bounds.intersects(obj.bounds)

  def check[A <: GameObject with Live[A]](obj: A): A = stage.filteredObjects.any(obj.detect).fold[A](obj.damaged, obj)

}
