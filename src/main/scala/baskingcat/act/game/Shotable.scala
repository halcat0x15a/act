package baskingcat.act.game

import baskingcat.act._

trait Shotable extends GameObject with HasDirection {

  val bulletTextures: Textures

  def shot(bullet: Bullet) = {
    Set(bullet)
  }

}
