package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait HasState[A <: State] {

  val state: A

}

trait HasDirection[A <: Direction] {

  val direction: A

}

trait Live[A <: State] extends GameObject with HasState[A] {

  val life: Int

  def damaged(implicit stage: Stage): Live[Damaging]

  def dead: Boolean = life <= 0 || bounds.top < 0

}

trait Movable[A <: State, B <: Direction] extends GameObject with HasState[A] with HasDirection[B] {

  val velocity: Vector2D[Float]

  def move(implicit ev: A <:< Moving): Movable[A, B]

  def apply(implicit stage: Stage): Movable[_ <: State, B]

}

trait Walkable[A <: State, B <: Direction] extends Movable[A, B] {

  def walk(implicit stage: Stage): Walkable[_ <: Moving, _ <: Direction]

}

trait Jumpable[A <: State, B <: Direction] extends Movable[A, B] {

  def jump(implicit ev: A <:< Standing, stage: Stage): Jumpable[_ <: Jumping, B]

}
