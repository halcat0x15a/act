package baskingcat.act

case class Background(properties: GameProperties, name: Symbol) extends GameObject {

  lazy val bounds: Rectangle = new Rectangle(properties.size)

}
