package baskingcat.act.gameplay

import baskingcat.act._

trait Live[A <: State] extends HasState[A] { obj: GameObject =>

  val life: Int

  def damaged(implicit stage: Stage): Live[Damaging]

  def dead: Boolean = life <= 0 || bounds.top < 0

}

