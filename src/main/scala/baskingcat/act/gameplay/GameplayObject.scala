package baskingcat.act.gameplay

import baskingcat.act._

abstract class GameplayObject extends GameObject {

  def update(implicit stage: Stage): Vector[GameplayObject]

}
