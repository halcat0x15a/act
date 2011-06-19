package baskingcat.act

case class Rectangle(location: Point, size: Dimension) {

  def this(location: Point) = this(location, Dimension(0, 0))

  def this(size: Dimension) = this(Vector2f(0, 0), size)

  def this(x: Float, y: Float, width: Float, height: Float) = this(Vector2f(x, y), Dimension(width, height))

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

  def intersects(rect: Rectangle) = rect.right > left && rect.bottom > top && rect.left < right && rect.top < bottom

}
