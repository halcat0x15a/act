package baskingcat.act.game

import baskingcat.act._
import math._

final case class Player(x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float)(override val id: Int, override val textures: Textures, override val width: Float, override val height: Float, override val bulletTextures: Textures) extends GameObject with HasDirection with Living with Movable with Jumpable with Shotable {

  def this(player: Player, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float) = this(x, y, vx, vy, direction, life)(player.id, player.textures, player.width, player.height, player.bulletTextures)

  override val invincible = false

  override val speed = 1f

  override val jumpPower = 12f

  val step = 15

}
