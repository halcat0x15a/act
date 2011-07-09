package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: State, B <: Direction](state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties) extends GameplayObject with HasState[A] with HasDirection[B] with Live[A] with Walkable[A, B] with Jumpable[A, B] {

  lazy val name = 'miku

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[C <: Direction](direction: C)(implicit stage: Stage) = {
    val s = state match {
      case m: Moving => m
      case _ => Walking()
    }
    val vx = direction match {
      case Forward() => velocity.x |+| Player.Speed
      case Backward() => velocity.x - Player.Speed
    }
    copy(state = s, direction = direction, velocity = velocity.copy(x = vx))
  }

  def jump(implicit stage: Stage, ev: A <:< Standing) = copy(state = Jumping(), velocity = velocity.copy(y = -Player.JumpPower))

  def apply(implicit stage: Stage) = {
    val ground = stage.blocks.find(block => block.bounds.top <= bounds.bottom && block.bounds.bottom > bounds.bottom && block.bounds.left <= bounds.right && block.bounds.right >= bounds.left)
    val x = bounds.location.x
    val y = ground match {
      case Some(block) if (velocity.y > 0) => block.bounds.location.y - bounds.size.height
      case _ => bounds.location.y
    }
    val vx = if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = ground ? 0f | (velocity.y |+| stage.gravity)
    val s = (vy === 0f && vx === 0f && ground.isDefined).fold[State](Normal(), state)
    copy(state = s, bounds = bounds.copy(location = Point(x, y)), velocity = Vector2D(vx, vy))
  }

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Player[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = {
    val vx = -velocity.x
    val vy = -velocity.y
    copy(state = Damaging(), velocity = Vector2D(vx, vy), life = life - 1)
  }

  def update(implicit stage: Stage) = {
    val walked = if (properties.input.isControllerRight)
      walk(Forward())
    else if (properties.input.isControllerLeft)
      walk(Backward())
    else
      this
    val jumped = walked match {
      case p: Player[_, _] => p.state match {
        case s: Standing if properties.input.isButtonPressed(0) => p.copy(state = s).jump
        case _ => p
      }
    }
    val moved = jumped match {
      case p: Player[_, _] => p.state match {
        case m: Moving => p.copy(state = m).move
        case _ => p
      }
    }
    moved.apply match {
      case p: Player[_, _] => stage.filteredObjects.any(p.detect).fold[Player[_, _]](p.damaged, p)
    }
  }

}

object Player {

  val Width = 64f

  val Height = 64f

  val Life = 1

  val Speed = 1f

  val JumpPower = 15f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float)(implicit properties: GameProperties) = {
    new Player(Normal(), Forward(), Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0f, 0f), Life)
  }

}
