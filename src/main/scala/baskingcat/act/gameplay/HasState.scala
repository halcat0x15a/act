package baskingcat.act.gameplay

import baskingcat.act._

trait HasState[A <: State] {

  val state: A

}
