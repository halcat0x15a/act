package baskingcat.act.game

import baskingcat.act._
import org.lwjgl.opengl._
import GL11._
import GL12._

final case class LifeGauge(life: Float) extends ACTObject {

  import LifeGauge._

  override val textures: Textures = Resource.textures("negi")

  override val width: Float = 16

  override val height: Float = 128

  override val x: Float = 32

  override val y: Float = ACT.height - height - 32

  override def draw() {
    glPushMatrix()
    super.draw()
    glColor3f(0, 0, 0)
    glRectf(left, bottom + height * (life / max), right, top)
    glPopMatrix()
  }

}

object LifeGauge {

  val max = 1000

}
