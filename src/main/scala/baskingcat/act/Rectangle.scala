package baskingcat.act

import scalaz._
import Scalaz._

case class Rectangle[T](location: Point[T], size: Dimension[T])(implicit num: Fractional[T]) {

  import num._

  lazy val x = location.x

  lazy val y = location.y

  lazy val width = size.width

  lazy val height = size.height

  lazy val left = x

  lazy val top = y

  lazy val right = x + width

  lazy val bottom = y + height

  lazy val two = one + one

  lazy val centerX = x + width / two

  lazy val centerY = y + height / two

  def contains(rect: Rectangle[T]) = rect.left >= left && rect.top >= top && rect.right <= right && rect.bottom <= bottom

  def intersects(rect: Rectangle[T]) = rect.right > left && rect.bottom > top && rect.left < right && rect.top < bottom

}

object Rectangle {

  def apply[T](location: Point[T])(implicit num: Fractional[T]) = {
    import num._
    new Rectangle(location, Dimension(zero, zero))
  }

  def apply[T](size: Dimension[T])(implicit num: Fractional[T]) = {
    import num._
    new Rectangle(Point(zero, zero), size)
  }

  def apply[T](x: T, y: T, width: T, height: T)(implicit num: Fractional[T]) = new Rectangle(Point(x, y), Dimension(width, height))

}
