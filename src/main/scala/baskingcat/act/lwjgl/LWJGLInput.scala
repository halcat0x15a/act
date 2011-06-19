package baskingcat.act

import scalaz._
import Scalaz._

import org.lwjgl.input._

class LWJGLInput extends Input {

  private lazy val controller = {
    val controller = (Controllers.getControllerCount > 0).option(Controllers.getController(0))
    controller |>| { _.setXAxisDeadZone(0.5f) }
    controller
  }

  private val keys = Array(Keyboard.KEY_Z, Keyboard.KEY_X, Keyboard.KEY_C)

  override def isButtonPressed(n: Int) = controller.some(_.isButtonPressed(n)).none(Keyboard.isKeyDown(keys(n)))

  override def isControllerUp = controller.some(_.getYAxisValue < 0).none(Keyboard.isKeyDown(Keyboard.KEY_UP))

  override def isControllerDown = controller.some(_.getYAxisValue > 0).none(Keyboard.isKeyDown(Keyboard.KEY_DOWN))

  override def isControllerLeft = controller.some(_.getXAxisValue < 0).none(Keyboard.isKeyDown(Keyboard.KEY_LEFT))

  override def isControllerRight = controller.some(_.getXAxisValue > 0).none(Keyboard.isKeyDown(Keyboard.KEY_RIGHT))

}

object LWJGLController {

  def create() {
    Keyboard.create()
    Controllers.create()
  }

  def poll() {
    Controllers.poll()
    Keyboard.poll()
  }

  def destroy() {
    Keyboard.isCreated.when {
      Keyboard.destroy()
    }
    Controllers.isCreated.when {
      Controllers.destroy()
    }
  }

}
