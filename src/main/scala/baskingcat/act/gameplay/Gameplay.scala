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

  private val gravity = 1f

  private val friction = 0.5f

  val objects = stage.objects

  val bounds = stage.viewport

  private type UpdateFunc = PartialFunction[GObj, GObj]

  def action: UpdateFunc = {
    case player: Player[_, _] => player.walk.jump
    case enemy: Enemy[_, _] => enemy.walk
    case obj: GameplayObject[_, _] => obj
  }

  def move: UpdateFunc = {
    case movable: Movable[_, _] => movable.move
    case obj: GameplayObject[_, _] => obj
  }

  def applyLaw: UpdateFunc = {
    case player @ Player(_, Vector2f(vx, vy), _, _) => {
      val vx2 = if (vx > 0)
        vx - friction
      else if (vx < 0)
        vx + friction
      else
        vx
      val vy2 = vy + gravity
      player.copy(velocity = Vector2f(vx2, vy2))
    }
    case obj: GameplayObject[_, _] => obj
  }

  def damage: UpdateFunc = {
    case live: Live[_, _] => live.damaged
    case obj: GameplayObject[_, _] => obj
  }

  def isDead(obj: GameplayObject[_, _]) = cond(obj) {
    case live: Live[_, _] => live.dead
  }

  def logic: Scene = if (properties.controller.isButtonPressed(5)) {
    Title()
  } else {
    val objects = ((_: GameplayObjects).map(action >>> move >>> applyLaw >>> damage).filterNot(isDead)).first.apply(stage.objects.partition(_.bounds.intersects(bounds))).fold(_ <+> _)
    objects.find(_.isInstanceOf[Player[_, _]]).map { player =>
      val location = {
        val x = if (player.bounds.location.x < properties.size.width / 2)
          0
        else if (player.bounds.location.x > stage.size.width - properties.size.width / 2)
          stage.size.width - properties.size.width
        else
          player.bounds.location.x - properties.size.width / 2
        val y = stage.size.height - properties.size.height
        Vector2f(x, y)
      }
      Gameplay(stage.copy(objects = objects))
    }.getOrElse(Title())
  }

}

