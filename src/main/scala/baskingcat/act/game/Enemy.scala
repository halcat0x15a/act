package baskingcat.act.game

import baskingcat.act._
import util._

final case class Enemy(
  id: Int,
  textures: Textures,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  vx: Float,
  vy: Float,
  direction: Direction.Value,
  life: Float
) extends GameObject with HasDirection with Damagable with Movable with Jumpable with Flyable with Shotable {

  def this(id: Int, textures: Textures, x: Float, y: Float, width: Float, height: Float, life: Float) = this(id, textures, x, y, width, height, 0, 0, Direction.Left, life)

  def this(enemy: Enemy, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float) = this(enemy.id, enemy.textures, x, y, enemy.width, enemy.height, vx, vy, direction, life)

  type T = this.type

  override val invincible = false
  override val speed = 1f
  override val power = 3
  override val jumpPower = 0f
  override val bulletTextures: Textures = null
  val itemTextures: Textures = null
  val moveType = Enemy.MoveType.Line

  def drop = if (Random.nextInt(100) < 10) {
    new Item(this)
  } else {
    null
  }

}

final object Enemy {

  object MoveType extends Enumeration {

    val Line = Value

  }

}
