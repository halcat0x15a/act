package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Enemy extends GameObject

case class Supu(status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int) extends Enemy with Live with Walkable {

  val obstacles = typeList[Cons[Bullet, Nil]]

  val speed: Float = 1f

  lazy val name = 'supu

  def movable(status: Status = status, dierction: Direction, bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy(status = status, direction = direction, bounds = bounds, velocity = velocity)

  def live(status: Status = status, life: Int = life) = copy(status = status, life = life)

}

object Supu {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 1

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Supu(Idling, Backward, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}
