package baskingcat.act.game

object Direction extends Enumeration {

  val Left, Right = Value

}

trait HasDirection {

  val direction: Direction.Value

  lazy val turn = {
    direction match {
      case Direction.Right => Direction.Left
      case Direction.Left => Direction.Right
    }
  }

}
