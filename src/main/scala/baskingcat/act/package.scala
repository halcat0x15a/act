package baskingcat

import scalaz._
import Scalaz._

package object act {

  type Point[A] = Vector2D[A]

  def Point[A](x: A, y: A)(implicit num: Numeric[A]) = Vector2D(x, y)

  def stream(name: String) = getClass.getClassLoader.getResourceAsStream(name)

  implicit def Vector2DSemigroup[A](implicit num: Numeric[A]) = {
    import num._
    semigroup[Vector2D[A]]((a, b) => a + b)
  }

  implicit def Vector2DZero[A](implicit num: Numeric[A]) = zero(Vector2D.Zero[A])

}
