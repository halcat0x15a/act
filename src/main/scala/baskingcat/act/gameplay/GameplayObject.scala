package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class GameplayObject[A <: State, B <: Direction] extends GameObject

trait Live[A <: State, B <: Direction] extends GameplayObject[A, B] {

  val life: Int

  def damaged(implicit stage: Stage): GameplayObject[A, B]

  def dead: Boolean = life <= 0 || bounds.top < 0

}

trait Movable[A <: State, B <: Direction] extends GameplayObject[A, B] {

  val velocity: Vector2[Float]

  def move: GameplayObject[Moving, B]

  def apply(implicit stage: Stage): GameplayObject[Moving, B]

}

trait Walkable[A <: State, B <: Direction] extends Movable[A, B] {

  def walk(implicit stage: Stage): GameplayObject[Walking, _ <: Direction]

}

trait Jumpable[A <: State, B <: Direction] extends Movable[A, B] {

  def jump(implicit stage: Stage): GameplayObject[Jumping, B]

}
