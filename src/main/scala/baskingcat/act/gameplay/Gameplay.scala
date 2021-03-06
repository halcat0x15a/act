package baskingcat.act.gameplay

import scala.PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._
import baskingcat.act.title.Title
import baskingcat.game._

case class Gameplay(stage: Stage)(implicit val properties: GameProperties) extends Scene {

  def this(name: String)(implicit properties: GameProperties) = this(Stage(name))

  val objects = stage.objects

  val bounds = stage.viewport

  private def coord(displaySize: Float, stageSize: Float, center: Float) = {
    val halfDisplaySize = displaySize / 2
    if (center < halfDisplaySize)
      0
    else if (center > stageSize - halfDisplaySize)
      stageSize - displaySize
    else
      center - halfDisplaySize
  }

  def logic: Scene = if (properties.input.isButtonPressed(5)) {
    Title()
  } else {
    val update = (_: GameplayObjects).withFilter {
      case l: Live[_] => !l.isDead(stage)
      case _ => true
    }.flatMap(_.update(stage))
    val objects = update.first.apply(stage.partitionedObjects).fold(_ <+> _)
    objects.find(_.isInstanceOf[Player.Type]).some[Scene] { player =>
      val location = {
        val x = coord(properties.size.width, stage.size.width, player.bounds.centerX)
        val y = coord(properties.size.height, stage.size.height, player.bounds.centerY)
        Vector2D(x, y)
      }
      Gameplay(stage.copy(objects = objects, viewport = bounds.copy(location = location)))
    }.none(Title())
  }

}
