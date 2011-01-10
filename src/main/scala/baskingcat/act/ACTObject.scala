package baskingcat.act

import com.baskingcat.game._
import java.nio._
import org.lwjgl._
import org.lwjgl.opengl._
import GL11._
import GL12._
import GL15._

abstract class ACTObject {

  val width: Float

  val height: Float

  val textures: Array[Texture]

  protected lazy val texImage = 0

  private lazy val indices = BufferUtils.createIntBuffer(4).put(Array(0, 1, 2, 3)).flip.asInstanceOf[IntBuffer]

  private lazy val vertecies = BufferUtils.createFloatBuffer(8).put(Array(0, 0, 0, height, width, height, width, 0)).flip.asInstanceOf[FloatBuffer]

  private lazy val (hTexRatio, vTexRatio) = {
    val texture = textures(0)
    if (texture.repeat) {
      (width / texture.width, height / texture.height)
    } else {
      (1f, 1f)
    }
  }

  private lazy val texCoords = BufferUtils.createFloatBuffer(8).put(Array[Float](0, vTexRatio, 0, 0, hTexRatio, 0, hTexRatio, vTexRatio)).flip.asInstanceOf[FloatBuffer]

  private lazy val buffers = {
    val buffers = BufferUtils.createIntBuffer(4)
    if (GLContext.getCapabilities.OpenGL15) {
      glGenBuffers(buffers)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glBufferData(GL_ARRAY_BUFFER, vertecies, GL_STATIC_DRAW)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(1))
      glBufferData(GL_ARRAY_BUFFER, texCoords, GL_STATIC_DRAW)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(2))
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    }
    buffers
  }

  private lazy val offset1 = BufferUtils.getOffset(vertecies)

  private lazy val offset2 = offset1 + BufferUtils.getOffset(texCoords)

  val x: Float

  val y: Float

  protected lazy val alpha = 1f

  def draw() {
    glPushMatrix()
    glTranslatef(x, y, 0)
    textures(texImage).bind()
    if (GLContext.getCapabilities.OpenGL15) {
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glVertexPointer(2, GL11.GL_FLOAT, 0, 0)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(1))
      glTexCoordPointer(2, GL11.GL_FLOAT, 0, offset1)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(2))
      glDrawRangeElements(GL_QUADS, 0, 3, 4, GL_UNSIGNED_INT, offset2)
    } else if (GLContext.getCapabilities.OpenGL12) {
      glVertexPointer(2, 0, vertecies)
      glTexCoordPointer(2, 0, texCoords)
      glDrawRangeElements(GL_QUADS, 0, 3, indices)
    } else {
      glDrawElements(GL_QUADS, indices)
    }
    glPopMatrix()
  }

  lazy val left = x

  lazy val right = x + width

  lazy val top = y + height

  lazy val bottom = y

  lazy val hCenter = x + width / 2

  lazy val bCenter = y + height / 2

  def delete() {
    println(getClass.getName)
    glDeleteBuffers(buffers)
    textures foreach (_.delete())
  } 

  override def finalize() {
    delete()
  }

}

