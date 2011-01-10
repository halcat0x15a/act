package baskingcat.act

import com.baskingcat.game._
import java.awt.image._
import java.io._
import java.nio._
import javax.imageio._
import org.lwjgl._
import opengl.GL11._

object Textures {

  def load(dataArray: Array[Array[Byte]], width: Int, height: Int, repeat: Boolean): Textures = {
    val dataArraySize = dataArray.size
    val buffers = BufferUtils.createIntBuffer(dataArraySize)
    glGenTextures(buffers)
    for (i <- 0 until dataArraySize) {
      val data = dataArray(i)
      val dataBuffer = BufferUtils.createByteBuffer(data.size).put(data).flip.asInstanceOf[ByteBuffer]
      glBindTexture(GL_TEXTURE_2D, buffers.get(i))
      glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, dataBuffer)
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
      glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    }
    new Textures(buffers, width, height, repeat)
  }

  def load(bufferedImages: Array[BufferedImage]): Textures = {
    val bufferedImage = bufferedImages(0)
    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight
    val hasAlpha = bufferedImage.getColorModel.hasAlpha
    val step = if (bufferedImage.getColorModel.hasAlpha) 4 else 3
    val dataArray = bufferedImages map (_.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData.sliding(step, step).toArray flatMap (_.reverse))
    load(dataArray, width, height, false)
  }

  def load(path: String): Textures= {
    val bufferedImage = try {
      ImageIO.read(new File(path))
    } catch {
      case e => throw Message.error(FileError(e, path))
    }
    load(Array(bufferedImage))
  }

}

class Textures(buffers: IntBuffer, val width: Int, val height: Int, val repeat: Boolean) {

  def bind(n: Int) = glBindTexture(GL_TEXTURE_2D, buffers.get(n))

  def delete() = glDeleteTextures(buffers)

}
