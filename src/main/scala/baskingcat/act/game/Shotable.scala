package baskingcat.act.game

import baskingcat.act._

trait Shotable extends GameObject {

  val bulletTextures: Array[Texture]

  def shot(bullet: Bullet) = {
    Set(bullet)
  }

}
