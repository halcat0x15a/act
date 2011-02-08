package baskingcat.act.game

import baskingcat.act._
import math._

trait Shotable extends GameObject with HasDirection {

  var bullets = Set.empty[Bullet]

  val bulletTextures: Textures

  val bulletWidth: Float

  val bulletHeight: Float

  def shot(_type: Bullet.Type.Value) {
    bullets ++= (_type match {
      case Bullet.Type.Normal => Set(Bullet(this, front, vCenter, 10, 0, direction)(1, 10 + abs(vx)))
      case Bullet.Type.Charge => Set(Bullet(this, front, vCenter, 10, 0, direction)(10, 10 + abs(vx)))
    })
    texImage = if (vx == 0) {
      if (direction == Direction.Right)
        8
      else
        9
    } else if (math.abs(vx.toInt) % 2 == 1) {
      if (direction == Direction.Right)
        10
      else
        11
    } else {
      if (direction == Direction.Right)
        12
      else
        13
    }
  }

  private def front = if (direction == Direction.Right) right else left - bulletWidth

}
