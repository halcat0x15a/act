package baskingcat.act.gameplay

import baskingcat.act._

trait HasStatus[A <: Status] { obj: GameplayObject =>

  val status: A

}
