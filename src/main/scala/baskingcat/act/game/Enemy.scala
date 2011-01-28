package baskingcat.act.game

import baskingcat.act._
import util._

final case class Enemy(x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float)(override val id: Int, override val textures: Textures, override val width: Float, override val height: Float) extends GameObject with HasDirection with Living with Damagable with Movable with Jumpable with Flyable with Shotable {

  def this(enemy: Enemy, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value, life: Float) = this(x, y, vx, vy, direction, life)(enemy.id, enemy.textures, enemy.width, enemy.height)

  override val invincible = false
  override val speed = 1f
  override val power: Float = 3
  override val jumpPower = 0f
  override val bulletTextures: Textures = Resource.textures("negi")
  val itemTextures: Textures = Resource.textures("negi")
  val moveType = Enemy.MoveType.Line

  lazy val item = if (Random.nextInt(100) < 10) {
    Some(new Item(this))
  } else {
    None
  }

}

final object Enemy {

  object MoveType extends Enumeration {

    val Line = Value

  }

}
