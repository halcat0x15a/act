package baskingcat.act.game

object Direction extends Enumeration {

  val Left, Right = Value

}

trait HasDirection {

  var direction: Direction.Value

  def turn {
    direction = direction match {
      case Direction.Right => Direction.Left
      case Direction.Left => Direction.Right
    }
  }

}
