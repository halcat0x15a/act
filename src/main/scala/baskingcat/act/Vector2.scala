package baskingcat.act

import scalaz._
import Scalaz._

case class Vector2[T](x: T, y: T)(implicit num: Numeric[T]) {

  import num._

  def -(vec: Vector2[T]) = Vector2(x - vec.x, y - vec.y)

}
