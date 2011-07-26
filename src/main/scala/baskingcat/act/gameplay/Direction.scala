package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasDirection {

  val direction: Direction
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
