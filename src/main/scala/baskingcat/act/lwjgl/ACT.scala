package baskingcat.act

import java.io._

import scala.annotation._
import scala.xml._

import scalaz._
import Scalaz._

import org.lwjgl._
import org.lwjgl.opengl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.util.glu.GLU._

import org.newdawn.slick.Color
import org.newdawn.slick.opengl.{ Texture, TextureLoader }
import org.newdawn.slick.util.Log

import baskingcat.game.opengl

import gameplay._

object ACT {

  private val Title = "Action Game"

  private val FPS = 60

  def texture(name: String) = TextureLoader.getTexture("PNG", stream(name))

  lazy val textures: Map[Symbol, Texture] = Map(
    'miku -> texture("miku/miku1.png"),
    'supu -> texture("supu/supu.png"),
    'negi -> texture("items/negi.png"),
    'block -> texture("blocks/block.png"),
    'title -> texture("backgrounds/title.png"))
/*
  def texture[A, B](obj: GameplayObject[A, B])(implicit mfa: ClassManifest[A], mfb: ClassManifest[B]) = {
    val state = mfa.erasure match {
      case Walking.Class => "w"
      case Jumping.Class => "j"
      case _ => "n"
    }
    val direction = mfb.erasure match {
      case Forward.Class => "f"
      case Backward.Class => "b"
      case Unknown.Class => ""
    }
    val char = obj match {
      case _: Player[_, _] => "miku"
      case _: Enemy[_, _] => "supu"
      case _: Block[_, _] => "block"
      case _: Bullet[_, _] => "negi"
    }
    char + state + direction + ".png"
  }
*/
  def init() {
    Display.setTitle(Title)
    val displayMode = Display.getAvailableDisplayModes.head
    Display.setDisplayMode(displayMode)
    Display.create()
    LWJGLController.create()
    glClearColor(0, 0, 0, 0)
    glViewport(0, 0, displayMode.getWidth, displayMode.getHeight)
    glMatrixMode(GL_PROJECTION)
    glLoadIdentity()
    gluOrtho2D(0, displayMode.getWidth, displayMode.getHeight, 0)
    glMatrixMode(GL_MODELVIEW)
    glEnable(GL_BLEND)
    glEnable(GL_TEXTURE_2D)
    glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
  }

  def render(scene: Scene) {
    glClear(GL_COLOR_BUFFER_BIT)
    opengl.glPushMatrix {
      glLoadIdentity()
      scene.bounds.location match {
        case Vector2f(x, y) => glTranslatef(-x, -y, 0)
      }
      Color.white.bind()
      scene.objects.withFilter(_.bounds.intersects(scene.bounds)).foreach { obj =>
        textures(obj.name).bind()
        opengl.glBegin(GL_QUADS) {
          glTexCoord2f(0, 0)
          glVertex2f(obj.bounds.left, obj.bounds.top)
          glTexCoord2f(1, 0)
          glVertex2f(obj.bounds.right, obj.bounds.top)
          glTexCoord2f(1, 1)
          glVertex2f(obj.bounds.right, obj.bounds.bottom)
          glTexCoord2f(0, 1)
          glVertex2f(obj.bounds.left, obj.bounds.bottom)
        }
      }
    }
  }

  @tailrec
  def run(scene: Scene) {
    LWJGLController.poll()
    val nextScene = if (Display.isCloseRequested) {
      return
    } else if (Display.isActive) {
      render(scene)
      scene.logic
    } else {
      (Display.isVisible || Display.isDirty) ! {
        render(scene)
      }
      scene
    }
    Display.sync(FPS)
    Display.update()
    run(nextScene)
  }

  def cleanup() {
    Display.isCreated ! {
      Display.destroy()
    }
    LWJGLController.destroy()
  }

  def main(args: Array[String]) {
    try {
      Log.info("init")
      init()
      Log.info("run")
      val size = Dimension(Display.getDisplayMode.getWidth, Display.getDisplayMode.getHeight)
      val input = new LWJGLInput
      implicit val properties = GameProperties(size, input)
      Log.info("test")
      run(title.Title())
    } catch {
      case e => e.printStackTrace()
    } finally {
      Log.info("cleanup")
      cleanup()
    }
    sys.exit()
  }

}
