package baskingcat.act

abstract class Scene {

  val properties: GameProperties

  val objects: Vector[GameObject]

  val bounds: Rectangle[Float]

  def logic: Scene

}
