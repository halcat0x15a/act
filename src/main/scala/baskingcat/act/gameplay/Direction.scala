package baskingcat.act.gameplay

sealed abstract class Direction

sealed abstract class Forward extends Direction

object Forward {

  val Class = classOf[Forward]

}

sealed abstract class Backward extends Direction

object Backward {

  val Class = classOf[Backward]

}

sealed abstract class Unknown extends Direction

object Unknown {

  val Class = classOf[Unknown]

}
