package baskingcat.act.game

import baskingcat.act._
import math._

final class Player(
override val id: Int,
override val textures: Array[Texture],
override val x: Float,
override val y: Float,
override val width: Float,
override val height: Float,
override val vx: Float,
override val vy: Float,
override val direction: Direction.Value,
override val life: Float
) extends GameObject with Movable with Jumpable with Shotable {

  def this(id: Int, textures: Array[Texture], x: Float, y: Float, width: Float, height: Float) = this(id, textures, x, y, width, height, 0, 0, Direction.Right, 100)

  def this(player: Player, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float) = this(player.id, player.textures, x, y, player.width, player.height, vx, vy, direction, life)

  override val invincible = false

  override val speed = 1f

  override val jumpPower = 12f

  val step = 15

  override val bulletTextures: Array[Texture] = Player.negi

}

object Player {

  val negi = Array(Texture.load("/home/halcat/projects/act/src/main/resources/items/negi.png"))

}
