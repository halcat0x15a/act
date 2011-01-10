package baskingcat.act

import com.baskingcat.game._
import java.nio._
import org.lwjgl._
import org.lwjgl.opengl._
import GL11._
import GL12._
import GL15._
import ARBBufferObject._
import ARBVertexBufferObject._

abstract class ACTObject {

  val width: Float

  val height: Float

  val textures: Textures

  protected lazy val texImage = 0

  private lazy val indices = BufferUtils.createIntBuffer(4).put(Array(0, 1, 2, 3)).flip.asInstanceOf[IntBuffer]

  private lazy val vertecies = BufferUtils.createFloatBuffer(8).put(Array(0, 0, 0, height, width, height, width, 0)).flip.asInstanceOf[FloatBuffer]

  private lazy val (hTexRatio, vTexRatio) = {
    if (textures.repeat) {
      (width / textures.width, height / textures.height)
    } else {
      (1f, 1f)
    }
  }

  private lazy val texCoords = BufferUtils.createFloatBuffer(8).put(Array[Float](0, vTexRatio, 0, 0, hTexRatio, 0, hTexRatio, vTexRatio)).flip.asInstanceOf[FloatBuffer]

  private lazy val buffers = {
    val buffers = BufferUtils.createIntBuffer(2)
    if (capabilities.OpenGL15) {
      glGenBuffers(buffers)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glBufferData(GL_ARRAY_BUFFER, verteciesSize + texCoordsSize, GL_STATIC_DRAW)
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertecies)
      glBufferSubData(GL_ARRAY_BUFFER, verteciesSize, texCoords)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(1))
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    } else if (capabilities.GL_ARB_vertex_buffer_object) {
      glGenBuffersARB(buffers)
      glBindBufferARB(GL_ARRAY_BUFFER_ARB, buffers.get(0))
      glBufferDataARB(GL_ARRAY_BUFFER_ARB, verteciesSize + texCoordsSize, GL_STATIC_DRAW_ARB)
      glBufferSubDataARB(GL_ARRAY_BUFFER_ARB, 0, vertecies)
      glBufferSubDataARB(GL_ARRAY_BUFFER_ARB, verteciesSize, texCoords)
      glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, buffers.get(1))
      glBufferDataARB(GL_ELEMENT_ARRAY_BUFFER_ARB, indices, GL_STATIC_DRAW_ARB)
    }
    buffers
  }

  private lazy val verteciesSize = 4 * 2 * 4

  private lazy val texCoordsSize = 4 * 2 * 4

  val x: Float

  val y: Float

  protected lazy val alpha = 1f

  private lazy val capabilities = GLContext.getCapabilities

  def draw() {
    glPushMatrix()
    glTranslatef(x, y, 0)
    textures.bind(texImage)
    if (capabilities.OpenGL15) {
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glVertexPointer(2, GL11.GL_FLOAT, 0, 0)
      glTexCoordPointer(2, GL11.GL_FLOAT, 0, verteciesSize)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(1))
      glDrawRangeElements(GL_QUADS, 0, 3, 4, GL_UNSIGNED_INT, 0)      
    } else if (capabilities.GL_ARB_vertex_buffer_object) {
      glBindBufferARB(GL_ARRAY_BUFFER_ARB, buffers.get(0))
      glVertexPointer(2, GL11.GL_FLOAT, 0, 0)
      glTexCoordPointer(2, GL11.GL_FLOAT, 0, verteciesSize)
      glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, buffers.get(1))
      if (capabilities.OpenGL12) {
	glDrawRangeElements(GL_QUADS, 0, 3, 4, GL_UNSIGNED_INT, 0)
      } else {
	glDrawElements(GL_QUADS, 4, GL_UNSIGNED_INT, 0)
      }
    } else if (capabilities.OpenGL12) {
      glVertexPointer(2, 0, vertecies)
      glTexCoordPointer(2, 0, texCoords)
      glDrawRangeElements(GL_QUADS, 0, 3, indices)
    } else {
      glVertexPointer(2, 0, vertecies)
      glTexCoordPointer(2, 0, texCoords)
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
    textures.delete()
  } 

  override def finalize() {
    delete()
  }

}

