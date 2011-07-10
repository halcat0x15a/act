package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: State, B <: Direction](state: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties) extends GameplayObject with HasState[A] with HasDirection[B] with Live[A] with Walkable[A, B] with Jumpable[A, B] {

  lazy val name = 'miku

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[C <: Direction](direction: C)(implicit stage: Stage) = {
    val s = state match {
      case _: Standing => new Walking with Standing
      case _: Flying => new Walking with Flying
    }
    val vx = direction match {
      case Forward() => velocity.x |+| Player.Speed
      case Backward() => velocity.x - Player.Speed
    }
    copy(state = s, direction = direction, velocity = velocity.copy(x = vx))
  }

  def jump(implicit stage: Stage, ev: A <:< Standing) = copy(state = new Jumping, velocity = velocity.copy(y = -Player.JumpPower))

  def apply(implicit stage: Stage) = {
    def hcheck(obj: GameObject) = obj.bounds.left < bounds.right && obj.bounds.right > bounds.left
    val grounds = stage.blocks.filter(block => block.bounds.top <= bounds.bottom && block.bounds.bottom >= bounds.bottom && hcheck(block))
    lazy val groundTop = grounds.map(_.bounds.top).min
    val ceilings = stage.blocks.filter(block => block.bounds.bottom >= bounds.top && block.bounds.top <= bounds.top && hcheck(block))
    lazy val ceilingBottom = ceilings.map(_.bounds.bottom).max
    lazy val vmargin = if (grounds.nonEmpty)
      bounds.bottom - groundTop
    else if (ceilings.nonEmpty)
      ceilingBottom - bounds.top
    else
      0
    def vcheck(obj: GameObject) = obj.bounds.top < bounds.bottom && obj.bounds.bottom > bounds.top
    val rwalls = stage.blocks.find(block => block.bounds.left <= bounds.right && block.bounds.right >= bounds.right && vcheck(block))
    lazy val rwallLeft = rwalls.map(_.bounds.left).min
    val lwalls = stage.blocks.find(block => block.bounds.right >= bounds.left && block.bounds.left <= bounds.left && vcheck(block))
    lazy val lwallRight = lwalls.map(_.bounds.right).max
    lazy val hmargin = if (rwalls.nonEmpty)
      bounds.right - rwallLeft
    else if (lwalls.nonEmpty)
      lwallRight - bounds.left
    else
      0
    val a = if (lwalls.nonEmpty)
      lwallRight
    else if (rwalls.nonEmpty)
      rwallLeft - bounds.size.width
    else
      bounds.location.x
    val b = if (grounds.size > 0)
      groundTop - bounds.size.height
    else if (ceilings.size > 0)
      ceilingBottom
    else
      bounds.location.y
    val (x, y) = if (vmargin < hmargin)
      bounds.location.x -> b
    else if (hmargin < vmargin)
      a -> bounds.location.y
    else
      a -> b
    val vx = if (lwalls.nonEmpty || rwalls.nonEmpty)
      0f
    else if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = (grounds.nonEmpty || (ceilings.nonEmpty && velocity.y < 0)) ? 0f | (velocity.y |+| stage.gravity)
    val s = state match {
      case f: Flying => if (grounds.nonEmpty)
        if (vx === 0)
          new Normal with Standing
        else
          new Walking with Standing
      else
        f
      case s: Standing => s
    }
    copy(state = s, bounds = bounds.copy(location = Point(x, y)), velocity = Vector2D(vx, vy))
  }

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Player[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = {
    val s = state match {
      case _: Standing => new Damaging with Standing
      case _: Flying => new Damaging with Flying
    }
    val vx = -velocity.x
    val vy = -velocity.y
    copy(state = s, velocity = Vector2D(vx, vy), life = life - 1)
  }

  def update(implicit stage: Stage) = {
    println(state.isInstanceOf[Standing].fold("Standing", "Flying"))
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
    new Player(new Normal with Standing, Forward(), Rectangle(Point(x, y), Dimension(Width, Height)), Vector2D(0f, 0f), Life)
  }

}
