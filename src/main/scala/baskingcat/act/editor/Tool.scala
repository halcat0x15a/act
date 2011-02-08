package baskingcat.act.editor

import baskingcat.act._
import java.awt.geom._
import java.awt.image._
import javax.swing.{ ImageIcon }
import swing._

class Tool(val _name: String, val dataType: String) extends ToggleButton {

  private val iconSize = 32

  private def convertIcon(image: BufferedImage) = {
    val sx = iconSize.toDouble / image.getWidth
    val sy = iconSize.toDouble / image.getHeight
    val op = new AffineTransformOp(AffineTransform.getScaleInstance(sx, sy), null)
    val dst = new BufferedImage(iconSize, iconSize, image.getType)
    op.filter(image, dst)
    dst
  }

  val imageData = Resource.imageDataMap(_name)

  icon = new ImageIcon(convertIcon(imageData.images(0)))

}
