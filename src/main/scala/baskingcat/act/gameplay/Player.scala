package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Player extends GameObject

case class Miku[A <: Status, B <: Direction](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val direction: Manifest[B]) extends Player with Live[A] with Walkable[A, B] with Jumpable[A, B] with Shootable[A, B] {

  lazy val name = Symbol("miku" + statusSuffix[A] + directionSuffix[B])

  val obstacles = typeList[Cons[Enemy, Cons[Bullet, Nil]]]

  val speed: Float = 7f

  override val acceleration: Float = 1f

  val jumpPower: Float = 20f

  lazy val bullet = Bullet[B, Miku[A, B]](this)

  def movable[A <: Status: Manifest, B <: Direction: Manifest](bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy[A, B](bounds = bounds, velocity = velocity)

  def shootable[A <: Status: Manifest] = copy[A, B]()

  def live[A <: Status: Manifest](life: Int) = copy[A, B](life = life)

}

object Player {

  val Width = 64f

  val Height = 64f

  val Life = 1

  val JumpPower = 20f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float)(implicit properties: GameProperties) = {
    new Miku[Idling, Forward](Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}

trait PlayerUpdate extends Update[Miku[_, _]] {

  implicit def update(obj: Miku[_, _]) = {
    GameObjects(obj).map {
      case p: Miku[_, _] => {
        if (properties.input.isControllerRight && rwalls(p).isEmpty)
          p.walk[Forward]
        else if (properties.input.isControllerLeft && lwalls(p).isEmpty)
          p.walk[Backward]
        else
          p
      }
    }.map {
      case p: Miku[_, _] =>
        (!(p.status <:< manifest[Jumping]) && properties.input.isButtonPressed(0)).fold[GameObject](p.jump, p)
    }.flatMap {
      case p: Miku[_, _] => properties.input.isButtonPressed(1).fold[(GameObject, Option[GameObject])](p.shoot.mapElements(identity, _.some), p -> none).toIndexedSeq
    }.map {
      case p: Miku[_, _] => movef(p)
    }.map {
      case p: Miku[_, _] => check(p)
    }
  }

}
