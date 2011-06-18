package baskingcat.act

case class Vector2f(x: Float, y: Float) {

  def this() = this(0, 0)

  def +(vec: Vector2f) = Vector2f(x + vec.x, y + vec.y)

  def -(vec: Vector2f) = Vector2f(x - vec.x, y - vec.y)

}
