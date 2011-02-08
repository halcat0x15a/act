package baskingcat.act.game

import baskingcat.act._

final case class Bullet(x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value)(override val id: Int, override val textures: Textures, override val width: Float, override val height: Float, override val power: Float, override val speed: Float) extends GameObject with HasDirection with Damagable with Movable with Fixing {

  def this(obj: Shotable, vx: Float, vy: Float, power: Float, speed: Float) = this(if (obj.direction == Direction.Right) obj.right else obj.left - obj.width, obj.y + obj.height / 2, vx, vy, obj.direction)(obj.id, obj.bulletTextures, obj.width, obj.height, power, speed)

  def this(bullet: Bullet, x: Float, y: Float, vx: Float, vy: Float, direction: Direction.Value) = this(x, y, vx, vy, direction)(bullet.id, bullet.textures, bullet.width, bullet.height, bullet.power, bullet.speed)

}

object Bullet {

  object Type extends Enumeration {
    val Normal, Charge = Value
  }

}
