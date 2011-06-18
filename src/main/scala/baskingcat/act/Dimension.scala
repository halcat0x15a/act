package baskingcat.act

case class Dimension(width: Float, height: Float) {

  lazy val halfWidth = width / 2

  lazy val halfHeight = height / 2

}
