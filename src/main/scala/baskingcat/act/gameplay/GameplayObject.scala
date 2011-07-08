package baskingcat.act.gameplay

import baskingcat.act._

abstract class GameplayObject extends GameObject {

  def update: this.type

}
