package baskingcat.act.title

import com.baskingcat.game._
import baskingcat.act._
import baskingcat.act.game._
import org.lwjgl.opengl.GL11._

final class Title extends Scene {

  val background = new Background(Resource.textures("title"))

  override def logic(controller: GameController): Scene = {
    if (controller.next) {
      if (controller.buttonPressed(1)) {
        return new Game(Resource.stages(0))
      }
    }
    this
  }

  override def render() {
    glClear(GL_COLOR_BUFFER_BIT)
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    background.draw
  }

}
