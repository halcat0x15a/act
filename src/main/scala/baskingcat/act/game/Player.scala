package baskingcat.act.game

import baskingcat.act._
import math._

final case class Player(
  id: Int,
  textures: Textures,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  vx: Float,
  vy: Float,
  direction: Direction.Value,
  life: Float) extends GameObject with HasDirection with Movable with Jumpable with Shotable {

  def this(id: Int, textures: Textures, x: Float, y: Float, width: Float, height: Float) = this(id, textures, x, y, width, height, 0, 0, Direction.Right, 100)

  def this(player: Player, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float) = this(player.id, player.textures, x, y, player.width, player.height, vx, vy, direction, life)

  override val invincible = false

  override val speed = 1f

  override val jumpPower = 12f

  val step = 15

  override val bulletTextures: Textures = Player.negi

}

object Player {

  val negi = Textures.load("/home/halcat/projects/act/src/main/resources/items/negi.png")

}
