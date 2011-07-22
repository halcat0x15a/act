package baskingcat.act

abstract class Scene {

  val properties: GameProperties

  val objects: GameObjects

  val bounds: Rectangle

  def logic: Scene

}
