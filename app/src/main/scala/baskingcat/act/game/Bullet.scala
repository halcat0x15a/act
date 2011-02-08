package baskingcat.act.game

import baskingcat.act._

final case class Bullet(obj: Shotable, override var x: Float, override var y: Float, _vx: Float, _vy: Float, override var direction: Direction.Value)(override val power: Float, override val speed: Float) extends GameObject with HasDirection with Damagable with Movable {

  override val id: String = obj.id

  override val textures: Textures = obj.bulletTextures

  override val width: Float = obj.bulletWidth

  override val height: Float = obj.bulletHeight

  override val fixed = true

}

object Bullet {

  object Type extends Enumeration {
    val Normal, Charge = Value
  }

}
