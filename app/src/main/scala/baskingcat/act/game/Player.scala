package baskingcat.act.game

import baskingcat.act._
import math._

final case class Player(override var x: Float, override var y: Float)(override val id: String, override val textures: Textures, override val width: Float, override val height: Float, override val bulletTextures: Textures, override val bulletWidth: Float, override val bulletHeight: Float) extends GameObject with HasDirection with Living with Movable with Jumpable with Shotable {

  override val fixed = false

  override var direction: Direction.Value = Direction.Right

  override var life: Float = LifeGauge.max

  override var invincible = false

  override val speed = 1f

  override val jumpPower = 12f

  val step = 15

  def get(item: Item) {

  }

}
