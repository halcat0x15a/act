package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasDirection[A <: Direction] {

  implicit val direction: Manifest[A]

  def directionSuffix[A <: Direction](implicit m: Manifest[A]) = if (m <:< manifest[Forward])
    "f"
  else if (m <:< manifest[Backward])
    "b"
  else
    undefined

}

sealed trait Direction

trait Forward extends Direction

trait Backward extends Direction
