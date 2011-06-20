package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class GameplayObject[A <: State, B <: Direction](implicit val mfa: Manifest[A], val mfb: Manifest[B]) extends GameObject

trait Live[A <: State, B <: Direction] extends GameplayObject[A, B] {

  val life: Int

  def damaged(implicit stage: Stage): GameplayObject[A, B]

  def dead: Boolean = life <= 0 || bounds.top < 0

}

trait Movable[A <: State, B <: Direction] extends GameplayObject[A, B] {

  val velocity: Vector2[Float]

  def move(implicit ev: A <:< Moving): GameplayObject[_ <: Moving, B]

  def apply(implicit stage: Stage): GameplayObject[_ <: Moving, B]

}

trait Walkable[A <: State, B <: Direction] extends Movable[A, B] {

  def walk(implicit stage: Stage): GameplayObject[_ <: Moving, _ <: Direction]

}

trait Jumpable[A <: State, B <: Direction] extends Movable[A, B] {

  def jump(implicit ev: A <:< Standing, stage: Stage): GameplayObject[_ <: Jumping, B]

}
