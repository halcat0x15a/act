package baskingcat.act

class Background(texture: Texture) extends ACTObject {

  override val width = ACT.width.asInstanceOf[Float]

  override val height = ACT.height.asInstanceOf[Float]

  override val textures = Array(texture)

  override val x = 0f

  override val y = 0f

}
