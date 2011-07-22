package baskingcat.act.gameplay

trait HasDirection[A <: Direction] {

  val direction: Manifest[A]

}

sealed trait Direction

trait Forward extends Direction

trait Backward extends Direction
