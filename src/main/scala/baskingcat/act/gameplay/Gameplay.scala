package baskingcat.act.gameplay

import scala.PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._
import baskingcat.act.title.Title
import baskingcat.game._

case class Gameplay(stage: Stage)(implicit val properties: GameProperties) extends Scene {

  def this(name: String)(implicit properties: GameProperties) = this(Stage(name))

  implicit val s = stage

  val objects = stage.objects

  val bounds = stage.viewport

  type UpdateFunc = PartialFunction[GObj, GObj]

  val walk: UpdateFunc = {
    case walkable: Walkable[_, _] => walkable.walk
    case obj: GameplayObject[_, _] => obj
  }

  val jump: UpdateFunc = {
    case jumpable: Jumpable[_, _] => jumpable.jump
    case obj: GameplayObject[_, _] => obj
  }

  val move: UpdateFunc = {
    case movable: Movable[_, _] => movable.move
    case obj: GameplayObject[_, _] => obj
  }

  val applyLaw: UpdateFunc = {
    case movable: Movable[_, _] => movable.apply
    case obj: GameplayObject[_, _] => obj
  }

  val damaged: UpdateFunc = {
    case live: Live[_, _] => live.damaged
    case obj: GameplayObject[_, _] => obj
  }

  val update: GObj => GObj = walk >>> jump >>> move >>> applyLaw >>> damaged 

  def isDead(obj: GObj): Boolean = cond(obj) {
    case live: Live[_, _] => live.dead;false
  }

  def logic: Scene = if (properties.input.isButtonPressed(5)) {
    Title()
  } else {
    val objects = ((_: GameplayObjects).map(update).filterNot(isDead)).first.apply(stage.objects.partition(_.bounds.intersects(bounds))).fold(_ <+> _)
    objects.find(_.isInstanceOf[Player[_, _]]).some[Scene] { player =>
      val location = {
        val x = if (player.bounds.centerX < properties.size.halfWidth)
          0
        else if (player.bounds.centerX > stage.size.width - properties.size.halfWidth)
          stage.size.width - properties.size.width
        else
          player.bounds.centerX - properties.size.halfWidth
        val y = if (player.bounds.centerY < properties.size.halfHeight)
          0
        else if (player.bounds.centerY > stage.size.height - properties.size.halfHeight)
          stage.size.height - properties.size.height
        else
          player.bounds.centerY - properties.size.halfHeight
        Vector2f(x, y)
      }
      Gameplay(stage.copy(objects = objects, viewport = bounds.copy(location = location)))
    }.none(Title())
  }

}
