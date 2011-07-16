package baskingcat.act.gameplay

import baskingcat.act._

trait Live[A <: Status] extends HasStatus[A] { obj: GameplayObject =>

  val life: Int

  def detect(obj: GameObject): Boolean

  def damaged: Live[_ <: Damaging]

  def isDead: Boolean = life <= 0 || bounds.top < 0

}

