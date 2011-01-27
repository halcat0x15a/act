package baskingcat.act.game

import baskingcat.act._

final class Block(
  override val id: Int,
  private val t: Block.Type.Value,
  override val textures: Textures,
  override val x: Float,
  override val y: Float,
  override val width: Float,
  override val height: Float) extends GameObject with Fixing with Blockable with Landable {  

  override val vx = 0f
  override val vy = 0f
  override val invincible = true
  override val life = 0f
  override val friction = 0.5f

}

object Block {

  object Type extends Enumeration {

    val Normal = Value

  }

}

