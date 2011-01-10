package baskingcat.act.game

import baskingcat.act._

class Block(
  override val id: Int,
  private val t: Block.Type.Value,
  override val textures: Array[Texture],
  override val x: Float,
  override val y: Float,
  override val width: Float,
  override val height: Float) extends GameObject with Fixing with Blockable with Landable {  

  override val invincible = true
  override val life = 0f
  override val direction: Direction.Value = t match {
    case Block.Type.Normal => null
  }
  override val friction = 0.5f

}

object Block {

  object Type extends Enumeration {

    val Normal = Value

  }

}

