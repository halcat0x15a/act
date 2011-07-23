package baskingcat.act

import scalaz._
import Scalaz._

case class Rectangle(location: Point, size: Dimension) {

  lazy val x = location.x

  lazy val y = location.y

  lazy val width = size.width

  lazy val height = size.height

  lazy val left = x

  lazy val top = y

  lazy val right = x + width

  lazy val bottom = y + height

  lazy val centerX = x + width / 2

  lazy val centerY = y + height / 2

  def contains(rect: Rectangle) = rect.left >= left && rect.top >= top && rect.right <= right && rect.bottom <= bottom

  def intersectsh(rect: Rectangle) = rect.left < right && rect.right > left

  def intersectsv(rect: Rectangle) = rect.top < bottom && rect.bottom > top

  def intersects(rect: Rectangle) = intersectsh(rect) && intersectsv(rect)

}

object Rectangle {

  def apply(location: Point) = new Rectangle(location, mzero[Dimension])

  def apply(size: Dimension) = new Rectangle(mzero[Point], size)

  def apply(x: Float, y: Float, width: Float, height: Float) = new Rectangle(Point(x, y), Dimension(width, height))

}
