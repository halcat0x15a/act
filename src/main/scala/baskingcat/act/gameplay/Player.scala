package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: State, B <: Direction](state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties) extends GameObject with HasState[A] with HasDirection[B] with Live[A] with Walkable[A, B] with Jumpable[A, B] {

  lazy val name = 'miku

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk(implicit stage: Stage): Walkable[_ <: Moving, _ <: Direction] = {
    val vx = if (properties.input.isControllerRight)
      velocity.x |+| Player.Speed
    else if (properties.input.isControllerLeft) 
      velocity.x - Player.Speed
    else
      velocity.x
    val s = state match {
      case m: Moving => m
      case _ => Walking()
    }
    copy(state = s, velocity = velocity.copy(x = vx))
  }

  def ground(implicit stage: Stage) = stage.blocks.find(block => block.bounds.intersects(bounds) && block.bounds.bottom > bounds.bottom)

  def jump(implicit ev: A <:< Standing, stage: Stage) = {
    val vy = (properties.input.isButtonPressed(0) && !state.isInstanceOf[Jumping]) ? -Player.JumpPower | velocity.y
    copy(state = Jumping(), velocity = velocity.copy(y = vy))
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
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = ground ? 0f | (velocity.y |+| stage.gravity)
    copy(bounds = bounds.copy(location = Vector2D(x, y)), velocity = Vector2D(vx, vy))
  }

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = copy(state = Damaging(), life = stage.objects.any(detect).fold(life - 1, life))

}

object Player {

  val Width = 64f

  val Height = 64f

  val Life = 1

  val Speed = 1f

  val JumpPower = 10f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float)(implicit properties: GameProperties) = {
    new Player(Normal(), Forward(), Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0f, 0f), Life)
  }

}
