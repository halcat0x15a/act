package baskingcat.act.game

import baskingcat.act._

final case class Item(enemy: Enemy) extends GameObject {

  override val id = enemy.hashCode

  override val width = 32f

  override val height = 32f

  override val textures = enemy.itemTextures

  override val x = enemy.x

  override val y = enemy.y

  override val vx = 0f

  override val vy = 0f

}

object Item {

  object Type extends Enumeration {

    val Life = Value

  }

}
    
