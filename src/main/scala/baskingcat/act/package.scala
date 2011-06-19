package baskingcat

import scalaz._
import Scalaz._

package object act {

  type Point[A] = Vector2[A]

  def Point[A](x: A, y: A)(implicit num: Fractional[A]) = Vector2(x, y)

  def stream(name: String) = getClass.getClassLoader.getResourceAsStream(name)

  implicit def VectorSemigroup[A](implicit num: Fractional[A]) = {
    import num._
    semigroup[Vector2[A]]((a, b) => Vector2(a.x + b.x, a.y + b.y))
  }

}
