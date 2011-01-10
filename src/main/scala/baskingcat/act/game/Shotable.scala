package baskingcat.act.game

import baskingcat.act._

trait Shotable extends GameObject {

  val bulletTextures: Textures

  def shot(bullet: Bullet) = {
    Set(bullet)
  }

}
