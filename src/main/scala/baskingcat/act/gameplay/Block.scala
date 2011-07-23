package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Block extends GameObject

case class NormalBlock(bounds: Rectangle) extends Block {

  lazy val name = 'block

}

object Block {

  val Width = 32f

  val Height = 32f

  def apply(x: Float, y: Float) = {
    new NormalBlock(Rectangle(Point(x, y), Dimension(Width, Height)))
  }

}
