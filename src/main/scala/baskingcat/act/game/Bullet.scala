package baskingcat.act.game

import baskingcat.act._

final case class Bullet(
  id: Int,
  private val obj: Shotable,
  private val _type: Bullet.Type.Value,
  x: Float,
  y: Float,
  width: Float,
  height: Float,
  vx: Float,
  vy: Float) extends GameObject with HasDirection with Movable with Fixing with Damagable {

  def this(obj: Shotable, _type: Bullet.Type.Value) = this(obj.id,
    obj,
    _type,
    if (obj.direction == Direction.Right) obj.right else obj.left - obj.width,
    obj.y + obj.height / 2,
    obj.width,
    obj.height,
    10f,
    0f)

  def this(bullet: Bullet, x: Float, y: Float, vx: Float, vy: Float) = this(bullet.id, bullet.obj, bullet._type, x, y, bullet.width, bullet.height, vx, vy)

  override val invincible = true
  override val life = 0f
  override val speed = 10f
  override val textures = obj.bulletTextures
  override val power = _type match {
    case Bullet.Type.Normal => 1
    case Bullet.Type.Charge => 5
  }

  override val direction = obj.direction

}

object Bullet {

  object Type extends Enumeration {
    val Normal, Charge = Value
  }

}
