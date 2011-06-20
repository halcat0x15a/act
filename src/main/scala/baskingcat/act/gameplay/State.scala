package baskingcat.act.gameplay

sealed abstract trait State

sealed abstract trait Normal extends Standing

sealed abstract trait Moving extends State

sealed abstract trait Walking extends Moving with Standing

sealed abstract trait Jumping extends Moving

sealed abstract trait Standing extends State

object Normal {

  val Class = classOf[Normal]

}

object Walking {

  val Class = classOf[Walking]

}

object Jumping {

  val Class = classOf[Jumping]

}
