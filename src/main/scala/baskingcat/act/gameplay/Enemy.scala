package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Enemy extends GameObject

case class Supu(status: Status, direction: Direction, bounds: Rectangle, velocity: Vector2D, life: Int) extends Enemy with Live[Supu] with Walkable[Supu] {

  val obstacles = typeList[Cons[Bullet, Nil]]

  val speed: Float = 1f

  lazy val name = 'supu

  def status(status: Status): Supu = copy(status = status)

  def direction(dierction: Direction): Supu = copy(direction = direction)

  def life(life: Int): Supu = copy(life = life)

  def bounds(bounds: Rectangle): Supu = copy(bounds = bounds)

  def velocity(velocity: Vector2D): Supu = copy(velocity = velocity)

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
