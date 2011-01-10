package baskingcat.act.game

trait Jumpable extends GameObject {

  private val effort = 0.2f

  val jumpPower: Float

  def jump(onGround: Boolean) = {
    if (onGround) {
      jumpPower
    } else {
      vy + effort
    }
  }


}
