package baskingcat.act

case class Vector2D[T](x: T, y: T)(implicit num: Numeric[T]) {

  import num._

  def +(vec: Vector2D[T]): Vector2D[T] = copy(x + vec.x, y + vec.y)

  def -(vec: Vector2D[T]): Vector2D[T] = copy(x - vec.x, y - vec.y)

  def unary_- = copy(-x, -y)

}

object Vector2D {

  def Zero[T](implicit num: Numeric[T]) = Vector2D(num.zero, num.zero)

}
