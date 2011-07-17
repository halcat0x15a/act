package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live[A <: Status] extends GameplayObject with HasStatus[A] {

  val life: Int

  def detect(obj: GameplayObject): Boolean

  def damaged: Live[_ <: Damaging]

  def live(implicit stage: Stage) = stage.filteredObjects.any(detect).fold[GameplayObject](damaged, this)

  def isDead: Boolean = life <= 0

}

