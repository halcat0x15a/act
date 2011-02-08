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

abstract class ACTObject() {

  var x: Float

  var y: Float

  val width: Float

  val height: Float

  val textures: Textures

  var texImage = 0

  private lazy val indicesArray = Array(0, 1, 2, 3)

  lazy val indices = BufferUtils.createIntBuffer(indicesArray.size).put(indicesArray)

  private lazy val verteciesArray = Array(0, 0, width, 0, width, height, 0, height)

  lazy val vertecies = BufferUtils.createFloatBuffer(verteciesArray.size).put(verteciesArray)

  private lazy val texCoordsArray = {
    val (hTexRatio, vTexRatio) = {
      if (textures.repeat) {
        (width / textures.width, height / textures.height)
      } else {
        (1f, 1f)
      }
    }
    Array(0, 0, hTexRatio, 0, hTexRatio, vTexRatio, 0, vTexRatio)
  }

  lazy val texCoords = BufferUtils.createFloatBuffer(texCoordsArray.size).put(texCoordsArray)

  private lazy val buffers: IntBuffer = {
    val buffers = BufferUtils.createIntBuffer(2)
    if (ACT.capabilities.OpenGL15) {
      glGenBuffers(buffers)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glBufferData(GL_ARRAY_BUFFER, verteciesSize + texCoordsSize, GL_STATIC_DRAW)
      glBufferSubData(GL_ARRAY_BUFFER, 0, vertecies)
      glBufferSubData(GL_ARRAY_BUFFER, verteciesSize, texCoords)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(1))
      glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW)
    } else if (ACT.capabilities.GL_ARB_vertex_buffer_object) {
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

  private lazy val list = {
    val id = glGenLists(1)
    glNewList(id, GL_COMPILE)
    glBegin(GL_QUADS)
    0 until verteciesArray.size filter (_ % 2 == 0) foreach { i =>
      glTexCoord2f(texCoordsArray(i), texCoordsArray(i + 1))
      glVertex2f(verteciesArray(i), verteciesArray(i + 1))
    }
    glEnd()
    glEndList()
    id
  }

  private lazy val verteciesSize = 4 * 2 * 4

  private lazy val texCoordsSize = 4 * 2 * 4

  var alpha = 1f

  def draw() {
    indices.rewind()
    vertecies.rewind()
    texCoords.rewind()
    glPushMatrix()
    glTranslatef(x, y, 0)
    glColor4f(1, 1, 1, alpha)
    glEnable(GL_TEXTURE_2D)
    textures.bind(texImage)
    if (ACT.capabilities.OpenGL15) {
      glEnableClientState(GL_VERTEX_ARRAY)
      glEnableClientState(GL_TEXTURE_COORD_ARRAY)
      glBindBuffer(GL_ARRAY_BUFFER, buffers.get(0))
      glVertexPointer(2, GL11.GL_FLOAT, 0, 0)
      glTexCoordPointer(2, GL11.GL_FLOAT, 0, verteciesSize)
      glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, buffers.get(1))
      glDrawRangeElements(GL_QUADS, 0, 3, 4, GL_UNSIGNED_INT, 0)
      glDisableClientState(GL_VERTEX_ARRAY)
      glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    } else if (ACT.capabilities.GL_ARB_vertex_buffer_object) {
      glEnableClientState(GL_VERTEX_ARRAY)
      glEnableClientState(GL_TEXTURE_COORD_ARRAY)
      glBindBufferARB(GL_ARRAY_BUFFER_ARB, buffers.get(0))
      glVertexPointer(2, GL11.GL_FLOAT, 0, 0)
      glTexCoordPointer(2, GL11.GL_FLOAT, 0, verteciesSize)
      glBindBufferARB(GL_ELEMENT_ARRAY_BUFFER_ARB, buffers.get(1))
      if (ACT.capabilities.OpenGL12) {
        glDrawRangeElements(GL_QUADS, 0, 3, 4, GL_UNSIGNED_INT, 0)
      } else {
        glDrawElements(GL_QUADS, 4, GL_UNSIGNED_INT, 0)
      }
      glDisableClientState(GL_VERTEX_ARRAY)
      glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    } else if (ACT.capabilities.OpenGL12) {
      glEnableClientState(GL_VERTEX_ARRAY)
      glEnableClientState(GL_TEXTURE_COORD_ARRAY)
      glVertexPointer(2, 0, vertecies)
      glTexCoordPointer(2, 0, texCoords)
      glDrawRangeElements(GL_QUADS, 0, 3, indices)
      glDisableClientState(GL_VERTEX_ARRAY)
      glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    } else if (ACT.capabilities.OpenGL11) {
      glEnableClientState(GL_VERTEX_ARRAY)
      glEnableClientState(GL_TEXTURE_COORD_ARRAY)
      glVertexPointer(2, 0, vertecies)
      glTexCoordPointer(2, 0, texCoords)
      glDrawElements(GL_QUADS, indices)
      glDisableClientState(GL_VERTEX_ARRAY)
      glDisableClientState(GL_TEXTURE_COORD_ARRAY)
    } else { // List
      glCallList(list)
    }
    glDisable(GL_TEXTURE_2D)
    glPopMatrix()
  }

  def left = x

  def right = x + width

  def top = y + height

  def bottom = y

  def hCenter = x + width / 2

  def vCenter = y + height / 2

  private var deleted = false

  def delete() = if (!deleted) {
    glDeleteBuffers(buffers)
    deleted = true
  }

  override def finalize() {
    delete()
  }

}

