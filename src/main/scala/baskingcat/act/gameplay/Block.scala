package baskingcat.act.gameplay

import baskingcat.act._

case class Block[A <: State, B <: Direction](bounds: Rectangle, velocity: Vector2f) extends GameplayObject[A, B] {

  def this(x: Float, y: Float) = this(Rectangle(Vector2f(x, y), Dimension(Block.Width, Block.Height)), Vector2f(0, 0))

  lazy val name = 'block

}

object Block {

  val Width: Float = 32

  val Height: Float = 32

}
