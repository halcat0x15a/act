package baskingcat.act.game

trait Jumpable extends GameObject {

  private val effort = 0.2f

  assert(effort < Game.gravity)

  val jumpPower: Float

  def jump(onGround: Boolean) {
    vy = if (onGround) {
      jumpPower
    } else {
      vy + effort
    }
  }


}
