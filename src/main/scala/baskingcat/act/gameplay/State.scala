package baskingcat.act.gameplay

sealed trait State

sealed case class Normal() extends Standing

sealed trait Moving extends State

sealed case class Walking() extends Moving() with Standing

sealed case class Jumping() extends Moving()

sealed trait Standing extends State

sealed case class Damaging() extends State
