package baskingcat.act

case class Dimension(width: Float, height: Float) {

  lazy val halfWidth: Float = width / 2

  lazy val halfHeight: Float = height / 2

}
