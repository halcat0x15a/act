package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Player extends GameObject

case class Miku(status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int) extends Player with Live[Miku] with Walkable[Miku] with Jumpable[Miku] with Shootable[Miku] {

  lazy val name = Symbol("miku")// + statusSuffix + directionSuffix)

  val obstacles = typeList[Cons[Enemy, Cons[Bullet, Nil]]]

  val speed: Float = 7f

  override val acceleration: Float = 1f

  val jumpPower: Float = 20f

  lazy val bullet = Negi(this)

  def status(status: Status): Miku = copy(status = status)

  def direction(dierction: Direction): Miku = copy(direction = direction)

  def life(life: Int): Miku = copy(life = life)

  def bounds(bounds: Rectangle): Miku = copy(bounds = bounds)

  def velocity(velocity: Vector2D): Miku = copy(velocity = velocity)

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

  def update(obj: Miku) = {
    obj |> { m =>
      if (properties.input.isControllerRight && rwalls(m).isEmpty)
	m.walk(Forward)
      else if (properties.input.isControllerLeft && lwalls(m).isEmpty)
	m.walk(Backward)
      else
	m
    } |> { m =>
      if (!(m.status == Jumping) && properties.input.isButtonPressed(0))
	m.jump
      else
	m
    } |> { m =>
      if (properties.input.isButtonPressed(1))
	m.shoot.mapElements(identity, _.some)
      else
	m -> none
    } |> {
      case (m, b) => movef(m) -> b
    } |> {
      case (m, b) => check(m) -> b
    } |> {
      case (m, Some(b)) => GameObjects(m, b)
      case (m, None) => GameObjects(m)
    }
  }

}
