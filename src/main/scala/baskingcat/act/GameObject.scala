
package baskingcat.act

import scalaz._
import Scalaz._

import baskingcat.game._
import baskingcat.game.opengl._

abstract class GameObject {

  val name: Symbol

  val bounds: Rectangle[Float]

}
