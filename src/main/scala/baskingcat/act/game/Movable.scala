package baskingcat.act.game

trait Movable extends GameObject with HasDirection {

  val speed: Float

  def move(direction: Direction.Value, onGround: Boolean) = {
    val vec = direction match {
      case Direction.Left => -speed
      case Direction.Right => speed
    }
    vx + (if (onGround) vec else vec / 2)
  }

}
