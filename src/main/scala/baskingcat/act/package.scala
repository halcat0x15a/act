package baskingcat

import scalaz._
import Scalaz._

package object act {

  type GameObjects = Seq[GameObject]

  def GameObjects(obj: GameObject*) = Vector(obj: _*)

  type Point = Vector2D

  def Point(x: Float, y: Float) = Vector2D(x, y)

  def stream(name: String) = getClass.getClassLoader.getResourceAsStream(name)

  implicit def Vector2DSemigroup = semigroup[Vector2D]((a, b) => a + b)

  implicit def Vector2DZero = zero(Vector2D(0, 0))

  implicit def DimensionZero = zero(Dimension(0, 0))

}
