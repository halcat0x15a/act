package baskingcat.act.gameplay

sealed abstract class State

sealed abstract class Normal extends State

sealed abstract class Moving extends State

sealed abstract class Walking extends Moving

sealed abstract class Jumping extends Walking

object Normal {

  val Class = classOf[Normal]

}

object Walking {

  val Class = classOf[Walking]

}

object Jumping {

  val Class = classOf[Jumping]

}
