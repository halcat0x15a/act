package baskingcat.act.gameplay

import baskingcat.act._

case class Block(bounds: Rectangle[Float], velocity: Vector2D[Float]) extends GameplayObject {

  lazy val name = 'block

  def update = this

}

object Block {

  val Width = 32f

  val Height = 32f

  def apply(x: Float, y: Float) = {
    new Block(Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0, 0))
  }

}
