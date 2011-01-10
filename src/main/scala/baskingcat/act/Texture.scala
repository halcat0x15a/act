package baskingcat.act

import com.baskingcat.game._
import java.awt.image._
import java.io._
import java.nio._
import javax.imageio._
import org.lwjgl._
import opengl.GL11._

object Texture {

  def createTextureID = {
    val textureID = BufferUtils.createIntBuffer(1)
    glGenTextures(textureID)
    textureID.get(0)
  }

  def load(data: Array[Byte], width: Int, height: Int, repeat: Boolean): Texture = {
    val texID = createTextureID
    val dataBuffer = BufferUtils.createByteBuffer(data.size).put(data).flip.asInstanceOf[ByteBuffer]
    glBindTexture(GL_TEXTURE_2D, texID)
    glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, dataBuffer)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
    glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
    new Texture(texID, width, height, repeat)
  }

  def load(bufferedImage: BufferedImage): Texture = {
    val width = bufferedImage.getWidth
    val height = bufferedImage.getHeight
    val hasAlpha = bufferedImage.getColorModel.hasAlpha
    val step = if (bufferedImage.getColorModel.hasAlpha) 4 else 3
    val data = bufferedImage.getRaster.getDataBuffer.asInstanceOf[DataBufferByte].getData.sliding(step, step).toArray flatMap (_.reverse)
    load(data, width, height, false)
  }

  def load(path: String): Texture = {
    val bufferedImage = try {
      ImageIO.read(new File(path))
    } catch {
      case e => throw Message.error(FileError(e, path))
    }
    load(bufferedImage)
  }

}

class Texture(id: Int, val width: Int, val height: Int, val repeat: Boolean) {

  def bind() = glBindTexture(GL_TEXTURE_2D, id)

  def delete() = glDeleteTextures(id)

}
