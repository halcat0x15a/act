package baskingcat.act.gameplay

trait HasDirection[A <: Direction]

sealed trait Direction

trait Forward extends Direction

trait Backward extends Direction
