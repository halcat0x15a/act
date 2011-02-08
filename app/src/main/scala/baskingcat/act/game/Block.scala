package baskingcat.act.game

import baskingcat.act._

final case class Block(override var x: Float, override var y: Float)(override val id: String, private val _type: Block.Type.Value, override val textures: Textures, override val width: Float, override val height: Float) extends GameObject {

  override val fixed = true

  val friction = 0.5f

}

object Block {

  object Type extends Enumeration {

    val Normal = Value

  }

}
