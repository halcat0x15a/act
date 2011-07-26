package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Player extends GameObject

case class Miku(status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int) extends Player with Live with Walkable with Jumpable with Shootable {

  lazy val name = Symbol("miku")// + statusSuffix + directionSuffix)

  val obstacles = typeList[Cons[Enemy, Cons[Bullet, Nil]]]

  val speed: Float = 7f

  override val acceleration: Float = 1f

  val jumpPower: Float = 20f

  lazy val bullet = Negi[Miku](this)

  def live(status: Status = status, life: Int = life) = copy(status = status, life = life)

  def movable(status: Status = status, dierction: Direction, bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy(status = status, direction = direction, bounds = bounds, velocity = velocity)

  def shootable(status: Status = status) = copy(status = status)

}

object Miku {

  val Width = 64f

  val Height = 64f

  val Life = 1

  val JumpPower = 20f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float) = {
    new Miku(Idling, Forward, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}

trait PlayerUpdate extends Update[Miku] {

  implicit def MikuTo(obj: GameObject) = obj match {
    case miku: Miku => miku
  }

  def update(obj: Miku) = {
    GameObjects(obj).map { m =>
      if (!(m.status == Jumping) && properties.input.isButtonPressed(0))
	m.jump
      else
	m
    }.map { m =>
      if (properties.input.isButtonPressed(1))
	m.shoot.mapElements(identity, _.some)
      else
	m -> none
    }.map {
      case (m, b) => movef(m) -> b
    }.map {
      case (m, b) => check(m) -> b
    }.flatMap {
      case (m, Some(b)) => GameObjects(m, b)
      case (m, None) => GameObjects(m)
    }
  }

}
