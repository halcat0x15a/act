package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Enemy extends GameObject

case class Supu[A <: Status, C <: Direction](bounds: Rectangle, velocity: Vector2D, life: Int)(implicit val status: Manifest[A], val direction: Manifest[C]) extends Enemy with Live[A] with Walkable[A, C] {

  val obstacles = typeList[Cons[Bullet, Nil]]

  val speed: Float = 1f

  lazy val name = 'supu

  def movable[A <: Status: Manifest, B <: Direction: Manifest](bounds: Rectangle = bounds, velocity: Vector2D = velocity) = copy[A, B](bounds = bounds, velocity = velocity)

  def live[A <: Status: Manifest](life: Int) = copy[A, C](life = life)

}

object Supu {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 1

  val Regex = """enemy.*""".r

  def apply(x: Float, y: Float) = {
    new Supu[Idling, Backward](Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}
