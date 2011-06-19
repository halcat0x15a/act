package baskingcat.act

case class Dimension[T](width: T, height: T)(implicit num: Fractional[T]) {

  import num._

  lazy val two = one + one

  lazy val halfWidth = width / two

  lazy val halfHeight = height / two

}
