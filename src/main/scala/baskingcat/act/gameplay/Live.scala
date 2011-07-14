package baskingcat.act.gameplay

import baskingcat.act._

trait Live[A <: Status] extends HasStatus[A] { obj: GameObject =>

  val life: Int

  def detect(obj: GameObject): Boolean

  def damaged(implicit stage: Stage): Live[Damaging]

  def isDead: Boolean = life <= 0 || bounds.top < 0

}

