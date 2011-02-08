package baskingcat.act

import java.awt.image._

class ImageData(val images: Array[BufferedImage], val repeat: Boolean) {

  def this(image: BufferedImage, repeat: Boolean) = this(Array(image), repeat)

  val image = images(0)

  val width = image.getWidth

  val height = image.getHeight

}
