package baskingcat.act

import com.baskingcat.game._
import title._
import annotation._
import xml._
import org.lwjgl._
import opengl._
import GL11._
import util.glu.GLU._

final object ACT extends AbstractGame {

  val debug = true
  val title = "Action Game"
  val fps = 60
  val width = 800
  val height = 600
  val halfWidth = width / 2
  val halfHeight = height / 2
  lazy val capabilities = GLContext.getCapabilities
  lazy val controller = getController
  protected override lazy val scene = new Title

  @throws(classOf[LWJGLException])
  override def init() {
    Sys.initialize()
    Display.setTitle(title)
    val displayMode = Display.getAvailableDisplayModes.find(m => m.getWidth == width && m.getHeight == height) match {
      case Some(m) => m
      case None => {
        throw Message.systemError(new Exception)
      }
    }
    Display.setDisplayMode(displayMode)
    Display.setTitle(title)
    Display.setVSyncEnabled(true)
    Display.setSwapInterval(1)
    Display.create()
    glClearColor(0, 0, 0, 0)
    glViewport(0, 0, width, height)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluOrtho2D(0, width, 0, height)
    glEnable(GL_BLEND)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    glEnableClientState(GL_VERTEX_ARRAY)
    glEnableClientState(GL_TEXTURE_COORD_ARRAY)
    println(glGetString(GL_VERSION))
    println("VBO: " + (capabilities.GL_ARB_vertex_buffer_object || GLContext.getCapabilities.OpenGL15))
    println("FBO: " + (capabilities.GL_EXT_framebuffer_object || GLContext.getCapabilities.OpenGL30))
  }

  @throws(classOf[LWJGLException])
  @tailrec
  override def run(controller: GameController, scene: Scene) {
    Display.update()
    controller.poll()
    val nextScene: Scene = if (Display.isCloseRequested || input.Keyboard.isKeyDown(input.Keyboard.KEY_ESCAPE)) {
      return
    } else if (Display.isActive) {
      val nextScene = scene.logic(controller)
      scene.render()
      Display.swapBuffers()
      Display.sync(fps)
      nextScene
    } else {
      try {
        Thread.sleep(100)
      } catch {
        case e => throw Message.systemError(e)
      }
      if (Display.isVisible || Display.isDirty) {
        scene.render()
      }
      scene
    }
    run(controller, nextScene)
  }

  @throws(classOf[LWJGLException])
  override def cleanup() {
    Display.destroy()
    controller.destroy()
    Resource.destroy()
  }

  def getController = {
    def createMap(attr: String) = ((Resource.config \\ "controller" \ "input") flatMap (input => Map((input \ "@id").text.toInt -> (input \ ("@" + attr)).text.toInt))).toMap
    val keyMap = createMap("key")
    val buttonMap = createMap("button")
    new GameController(keyMap, buttonMap)
  }

}
