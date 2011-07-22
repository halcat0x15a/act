package baskingcat.act.gameplay

import baskingcat.act._

trait Movable[A <: Status, B <: Form, C <: Direction] extends HasStatus[A] with HasForm[B] with HasDirection[C] { obj: GameObject =>

  val velocity: Vector2D

  def movable(bounds: Rectangle): GameObject

  def move: GameObject = movable(bounds.copy(location = bounds.location + velocity))

  def hcheck(obj: GameObject) = obj.bounds.left < bounds.right && obj.bounds.right > bounds.left

  def vcheck(obj: GameObject) = obj.bounds.top < bounds.bottom && obj.bounds.bottom > bounds.top

  def grounds(implicit stage: Stage) = stage.blocks.filter(block => block.bounds.top <= bounds.bottom && block.bounds.bottom >= bounds.bottom && hcheck(block))

  def ceilings(implicit stage: Stage) = stage.blocks.filter(block => block.bounds.bottom >= bounds.top && block.bounds.top <= bounds.top && hcheck(block))

  def rwalls(implicit stage: Stage) = stage.blocks.find(block => block.bounds.left <= bounds.right && block.bounds.right >= bounds.right && vcheck(block))

  def lwalls(implicit stage: Stage) = stage.blocks.find(block => block.bounds.right >= bounds.left && block.bounds.left <= bounds.left && vcheck(block))

}
