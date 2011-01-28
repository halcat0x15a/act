package baskingcat.act.game

import baskingcat.act._

final case class Block(x: Float, y: Float, vx: Float, vy: Float)(override val id: Int, private val _type: Block.Type.Value, override val textures: Textures, override val width: Float, override val height: Float) extends GameObject with Fixing with Blockable with Landable {

  def this(block: Block, x: Float, y: Float, vx: Float, vy: Float) = this(x, y, vx, vy)(block.id, block._type, block.textures, block.width, block.height)

  override val friction = 0.5f

}

object Block {

  object Type extends Enumeration {

    val Normal = Value

  }

}
