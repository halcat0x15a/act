package baskingcat.act

case class Background(name: Symbol)(implicit properties: GameProperties) extends GameObject {

  lazy val bounds = Rectangle(properties.size)

}
