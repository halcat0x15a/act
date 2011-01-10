package baskingcat.act.game

import baskingcat.act._

class Item(enemy: Enemy) extends GameObject {

  override val id = enemy.hashCode

  override val width = 32f

  override val height = 32f

  override val textures = enemy.itemTextures

  override val x = enemy.x

  override val y = enemy.y

  override val direction = enemy.direction

  override val invincible = true

  override val life = 0f

}

object Item {

  object Type extends Enumeration {

    val Life = Value

  }

}
    
