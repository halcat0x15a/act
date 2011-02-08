package baskingcat.act.game

import baskingcat.act._

final case class Item(enemy: Enemy) extends GameObject {

  override val id = enemy.id

  override val width = 32f

  override val height = 32f

  override val textures = enemy.itemTextures

  override var x = enemy.x

  override var y = enemy.y

  override val fixed = false

}

