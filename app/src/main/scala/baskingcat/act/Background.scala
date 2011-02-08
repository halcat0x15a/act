package baskingcat.act

class Background(override val textures: Textures) extends ACTObject {

  override var x: Float = 0

  override var y: Float = 0

  override val width = ACT.width.asInstanceOf[Float]

  override val height = ACT.height.asInstanceOf[Float]

}
