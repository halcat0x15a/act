package baskingcat.act.clear

import com.baskingcat.game._
import baskingcat.act._
import title._
import org.lwjgl.opengl.GL11._
import org.lwjgl.input._

final class Clear(time: Float) extends Scene {

  override def logic(controller: GameController): Scene = {
    if (controller.next) {
      if (controller.buttonPressed(1)) {
        return new Title
      }
    }
    this
  }

  override def render() {
    glClear(GL_COLOR_BUFFER_BIT)
  }

  override def dispose() {
  }

}
