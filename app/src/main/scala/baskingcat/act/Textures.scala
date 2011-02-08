package baskingcat.act

import com.baskingcat.game._
import java.awt.image._
import java.io._
import java.nio._
import javax.imageio._
import org.lwjgl._
import opengl.GL11._
import opengl.GL14._
import util.glu.GLU._

object Textures {

  def load(dataArray: Array[Array[Byte]], width: Int, height: Int, repeat: Boolean): Textures = {
    val dataArraySize = dataArray.size
    val buffers = BufferUtils.createIntBuffer(dataArraySize)
    glGenTextures(buffers)
    for (i <- 0 until dataArraySize) {
      val data = dataArray(i)
      val dataBuffer = BufferUtils.createByteBuffer(data.size).put(data).flip.asInstanceOf[ByteBuffer]
      glBindTexture(GL_TEXTURE_2D, buffers.get(i))
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
      glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
      if (ACT.capabilities.OpenGL14) {
        glTexParameteri(GL_TEXTURE_2D, GL_GENERATE_MIPMAP, GL_TRUE)
        // 元画像は透明
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, dataBuffer)
      } else {
        gluBuild2DMipmaps(GL_TEXTURE_2D, GL_RGBA, width, height, GL_RGBA, GL_UNSIGNED_BYTE, dataBuffer)
      }
    }
    new Textures(buffers, dataArraySize, width, height, repeat)
  }

  def load(bufferedImages: Array[BufferedImage], repeat: Boolean): Textures = {
    val bufferedImage = bufferedImages(0)
    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight
    val hasAlpha = bufferedImage.getColorModel.hasAlpha
    val colorStep = if (bufferedImage.getColorModel.hasAlpha) 4 else 3
    val widthSize = width * colorStep
    val dataArray = bufferedImages map { image =>
      (image.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData.reverse.sliding(colorStep, colorStep).toArray flatMap (_.reverse)).sliding(widthSize, widthSize).toArray flatMap(_.reverse)
    }
    load(dataArray, width, height, repeat)
  }

}

class Textures(buffers: IntBuffer, val size: Int, val width: Int, val height: Int, val repeat: Boolean) {

  def bind(n: Int) = glBindTexture(GL_TEXTURE_2D, buffers.get(n))

  private var deleted = false

  def delete() = if (!deleted) {
    glDeleteTextures(buffers)
    deleted = true
  }

}
