package baskingcat.act.gameplay

sealed abstract class Direction

sealed case class Forward() extends Direction

sealed case class Backward() extends Direction
