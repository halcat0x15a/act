package baskingcat.act

case class Vector2D[T](x: T, y: T)(implicit num: Fractional[T]) {

  import num._

  def +(vec: Vector2D[T]) = Vector2D(x + vec.x, y + vec.y)

  def -(vec: Vector2D[T]) = Vector2D(x - vec.x, y - vec.y)

}
