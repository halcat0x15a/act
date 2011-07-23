package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait GameplayObject[A <: GameObject] {

  val properties: GameProperties

  val stage: Stage

  def update(obj: A): GameObjects

  def isDead(live: GameObject with Live[_]) = live.life <= 0 || !stage.bounds.intersects(live.bounds)

  def check(live: GameObject with Live[_]) = stage.filteredObjects.any(live.detect).fold[GameObject](live.damaged, live)

  def grounds(obj: GameObject) = stage.blocks.filter(block => block.bounds.top <= obj.bounds.bottom && block.bounds.bottom >= obj.bounds.bottom && obj.bounds.intersectsh(block.bounds))

  def ceilings(obj: GameObject) = stage.blocks.filter(block => block.bounds.bottom >= obj.bounds.top && block.bounds.top <= obj.bounds.top && obj.bounds.intersectsh(block.bounds))

  def rwalls(obj: GameObject) = stage.blocks.filter(block => block.bounds.left <= obj.bounds.right && block.bounds.right >= obj.bounds.right && obj.bounds.intersectsv(block.bounds))

  def lwalls(obj: GameObject) = stage.blocks.filter(block => block.bounds.right >= obj.bounds.left && block.bounds.left <= obj.bounds.left && obj.bounds.intersectsv(block.bounds))

  def fix(obj: GameObject with Movable[_, _]): GameObject with Movable[_, _] = {
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
          fix(obj.movable(obj.bounds.copy(location = location)))
        else
          obj
      }
    }
  }

}
