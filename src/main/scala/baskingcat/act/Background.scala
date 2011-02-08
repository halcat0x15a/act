package baskingcat.act

class Background(override val textures: Textures) extends ACTObject {

  override val width = ACT.width.asInstanceOf[Float]

  override val height = ACT.height.asInstanceOf[Float]

  override val x = 0f

  override val y = 0f

}
