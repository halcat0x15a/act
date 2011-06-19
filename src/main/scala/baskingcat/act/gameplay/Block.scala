package baskingcat.act.gameplay

import baskingcat.act._

case class Block[A <: State, B <: Direction](bounds: Rectangle, velocity: Vector2f) extends GameplayObject[A, B] {

  lazy val name = 'block

}

object Block {

  val Width = 32f

  val Height = 32f

  def apply(x: Float, y: Float) = {
    new Block[Normal, Unknown](Rectangle(Vector2f(x, y), Dimension(Width, Height)), Vector2f(0, 0))
  }

}
