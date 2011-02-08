package baskingcat.act.game

trait Movable extends GameObject with HasDirection {

  val speed: Float

  def move(onGround: Boolean) {
    val vec = (direction match {
      case Direction.Left => -speed
      case Direction.Right => speed
    }) / (if (onGround) 1 else 5f)
    vx += vec
  }

}
