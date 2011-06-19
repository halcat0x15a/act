package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: State, B <: Direction](bounds: Rectangle, velocity: Vector2f, life: Int)(implicit properties: GameProperties, mfa: Manifest[A], mfb: Manifest[B]) extends GameplayObject[A, B] with Live[A, B] with Walkable[A, B] with Jumpable[A, B] {

  lazy val name = 'miku

  def move = copy(bounds = bounds.copy(location = bounds.location + velocity))

  def walk(implicit stage: Stage) = {
    val vx = if (properties.input.isControllerRight)
      velocity.x + Player.Speed
    else if (properties.input.isControllerLeft)
      velocity.x - Player.Speed
    else
      velocity.x
    copy(velocity = velocity.copy(x = vx))
  }

  def ground(implicit stage: Stage) = stage.blocks.find(block => block.bounds.intersects(bounds) && block.bounds.bottom > bounds.bottom)

  def jump(implicit stage: Stage) = {
    val vy = (properties.input.isButtonPressed(0) && ground.isDefined) ? -Player.JumpPower | velocity.y
    copy(velocity = velocity.copy(y = vy))
  }

  def apply(implicit stage: Stage) = {
    val x = bounds.location.x
    val y = if (velocity.y > 0)
      ground.some(_.bounds.location.y - bounds.size.height).none(bounds.location.y)
    else
      bounds.location.y
    val vx = if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x + stage.friction
    else
      velocity.x
    val vy = ground ? 0f | (velocity.y + stage.gravity)
    copy(bounds = bounds.copy(location = Vector2f(x, y)), velocity = Vector2f(vx, vy))
  }

  def detect(obj: GameplayObject[_, _]) = !obj.isInstanceOf[Block[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = copy(life = stage.objects.any(detect).fold(life - 1, life))

}

object Player {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 1

  val Speed: Float = 1

  val JumpPower: Float = 10

  def apply[A <: State, B <: Direction](x: Float, y: Float)(implicit properties: GameProperties, mfa: Manifest[A], mfb: Manifest[B]) = {
    new Player(Rectangle(Vector2f(x, y), Dimension(Width, Height)), Vector2f(0, 0), Player.Life)
  }

}
