package baskingcat

import scalaz._
import Scalaz._

package object act {

  def stream(name: String) = getClass.getClassLoader.getResourceAsStream(name)

  implicit def VectorSemigroup = semigroup[Vector2[Float]]((a, b) => Vector2(a.x |+| b.x, a.y |+| b.y))

}
