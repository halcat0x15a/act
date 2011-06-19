package baskingcat.act.gameplay

import baskingcat.act._

case class Block[A <: State, B <: Direction](bounds: Rectangle[Float], velocity: Vector2[Float]) extends GameplayObject[A, B] {

  lazy val name = 'block

}

object Block {

  val Width = 32f

  val Height = 32f

  def apply(x: Float, y: Float) = {
    new Block[Normal, Unknown](Rectangle(Point(x, y), Dimension(Width, Height)), Vector2(0, 0))
  }

}
