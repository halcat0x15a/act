package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasDirection[A <: Direction] {

  implicit val direction: Manifest[A]

  def directionSuffix = direction match {
    case Forward.Manifest => "f"
    case Backward.Manifest => "b"
  }

}

sealed trait Direction

trait Forward extends Direction

object Forward {
  val Manifest = manifest[Forward]
}

trait Backward extends Direction

object Backward {
  val Manifest = manifest[Backward]
}
