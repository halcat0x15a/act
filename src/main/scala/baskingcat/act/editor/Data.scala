package baskingcat.act.editor

import baskingcat.act._
import java.awt.{ Point, Rectangle }
import java.awt.image._

class Data(private val _type: String, val imageData: ImageData, val x: Int, val y: Int, val width: Int, val height: Int) {

  val step = 4

  def this(_type: String, imageData: ImageData, x: Int, y: Int) = this(_type, imageData, x, y, imageData.width, imageData.height)

  def this(data: Data, x: Int, y: Int, width: Int, height: Int) = this(data._type, data.imageData, x, y, width, height)

  val fixY = ACTEditor.maxHeight - y

  private val rect = new Rectangle(x, fixY, width, height)

  def intersects(data: Data) = rect.intersects(data.rect)

  def contains(point: Point) = rect.contains(point)

  val images = imageData.images map (_.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData.sliding(step, step).toArray flatMap (_.reverse))

  implicit def intToString(int: Int) = int.toString

  def toXML = <data id={ hashCode } type={ _type } x={ x } y={ y - height } width={ width } height={ height }>
                <images width={ imageData.width } height={ imageData.height } repeat={ imageData.repeat.toString }>
                  { images map (image => <image>{ image.mkString(",") }</image>) }
                </images>
              </data>

  def toXML(id: String) = <data id={ hashCode } type={ _type } x={ x } y={ y - height } width={ width } height={ height } images={ new StringBuilder("url(#").append(id).append(")").toString }></data>

}
