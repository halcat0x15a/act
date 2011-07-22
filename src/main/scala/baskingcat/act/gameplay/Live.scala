package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait Live[A <: Status] extends GameObject with HasStatus[A] {

  val life: Int

  def detect(obj: GameObject): Boolean

  def damaged: Live[_ <: Damaging]

  def live(implicit stage: Stage) = stage.filteredObjects.any(detect).fold[GameObject](damaged, this)

  def isDead(implicit stage: Stage): Boolean = life <= 0 || !stage.bounds.intersects(bounds)

}

