package baskingcat.act.game

import baskingcat.act._

trait Shotable extends GameObject with Product with HasDirection {

  val bulletTextures: Textures

  def shot(_type: Bullet.Type.Value) = _type match {
    case Bullet.Type.Normal => Set(new Bullet(this, 10, 0, 1, 10))
    case Bullet.Type.Charge => Set(new Bullet(this, 10, 0, 10, 10))
  }

}
