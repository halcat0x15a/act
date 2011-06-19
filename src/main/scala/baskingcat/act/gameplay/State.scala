package baskingcat.act.gameplay

sealed abstract class State

sealed abstract class Normal extends State

object Normal {

  val Class = classOf[Normal]

}

sealed abstract class Moving extends State

sealed abstract class Walking extends Moving

object Walking {

  val Class = classOf[Walking]

}

sealed abstract class Jumping extends Moving

object Jumping {

  val Class = classOf[Jumping]

}
