package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait HasDirection[A <: GameObject with HasDirection[A]] { obj: GameObject =>

  val direction: Direction

  def direction(direction: Direction): A
/*
  def directionSuffix = direction match {
    case Forward.Manifest => "f"
    case Backward.Manifest => "b"
  }
*/
}

sealed trait Direction

case object Forward extends Direction

case object Backward extends Direction
