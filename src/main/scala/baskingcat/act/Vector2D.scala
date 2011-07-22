package baskingcat.act

case class Vector2D(x: Float, y: Float) {

  def +(vec: Vector2D): Vector2D = copy(x + vec.x, y + vec.y)

  def -(vec: Vector2D): Vector2D = copy(x - vec.x, y - vec.y)

  def unary_- = copy(-x, -y)

}
