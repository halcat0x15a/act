package baskingcat.act.title

import baskingcat.act._
import baskingcat.act.gameplay._

import scalaz._
import Scalaz._

case class Title(implicit val properties: GameProperties) extends Scene {

  val objects = Vector(Background('title))

  val bounds = Rectangle(properties.size)

  def logic: Scene = {
    if (properties.input.isButtonPressed(0))
      new Gameplay("stages/test.svg")
    else
      this
  }

}

