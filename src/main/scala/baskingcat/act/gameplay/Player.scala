package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: Status, B <: Direction](status: A, direction: B, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties) extends GameplayObject with HasStatus[A] with HasDirection[B] with Live[A] with Walkable[A, B] with Jumpable[A, B] with Shootable[A, B] {

  lazy val name = 'miku

  private def self(a: Any) = a match {
    case p: Player[_, _] => p
  }

  def move(implicit ev: A <:< Moving) = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[C <: Direction](direction: C)(implicit stage: Stage) = {
    val s = status match {
      case _: Standing => new Walking with Standing
      case _: Flying => new Walking with Flying
    }
    val vx = direction match {
      case _: Forward => velocity.x |+| Player.Speed
      case _: Backward => velocity.x - Player.Speed
    }
    copy(status = s, direction = direction, velocity = velocity.copy(x = vx))
  }

  def jump(implicit stage: Stage, ev: A <:< Standing) = copy(status = new Jumping, velocity = velocity.copy(y = -Player.JumpPower))

  def hcheck(obj: GameObject) = obj.bounds.left < bounds.right && obj.bounds.right > bounds.left

  def vcheck(obj: GameObject) = obj.bounds.top < bounds.bottom && obj.bounds.bottom > bounds.top

  def grounds(implicit stage: Stage) = stage.blocks.filter(block => block.bounds.top <= bounds.bottom && block.bounds.bottom >= bounds.bottom && hcheck(block))

  def ceilings(implicit stage: Stage) = stage.blocks.filter(block => block.bounds.bottom >= bounds.top && block.bounds.top <= bounds.top && hcheck(block))

  def rwalls(implicit stage: Stage) = stage.blocks.find(block => block.bounds.left <= bounds.right && block.bounds.right >= bounds.right && vcheck(block))

  def lwalls(implicit stage: Stage) = stage.blocks.find(block => block.bounds.right >= bounds.left && block.bounds.left <= bounds.left && vcheck(block))

  def fix(implicit stage: Stage) = {
    lazy val groundTop = grounds.map(_.bounds.top).min
    lazy val ceilingBottom = ceilings.map(_.bounds.bottom).max
    lazy val vmargin = if (grounds.nonEmpty)
      bounds.bottom - groundTop
    else if (ceilings.nonEmpty)
      ceilingBottom - bounds.top
    else
      0
    lazy val rwallLeft = rwalls.map(_.bounds.left).min
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
    copy(bounds = bounds.copy(location = Point(x, y)))
  }

  def apply(implicit stage: Stage) = {
    val vx = if (lwalls.nonEmpty || rwalls.nonEmpty)
      0f
    else if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = (grounds.nonEmpty || (ceilings.nonEmpty && velocity.y < 0)) ? 0f | (velocity.y |+| stage.gravity)
    val s = status match {
      case f: Flying => if (grounds.nonEmpty)
        if (vx === 0)
          new Normal with Standing
        else
          new Walking with Standing
      else
        f
      case s: Standing => s
    }
    copy(status = s, velocity = Vector2D(vx, vy))
  }

  def detect(obj: GameObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Player[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = {
    val s = status match {
      case _: Standing => new Damaging with Standing
      case _: Flying => new Damaging with Flying
    }
    copy(status = s, velocity = -velocity, life = life - 1)
  }

  def shoot = {
    val s = status match {
      case _: Standing => new Shooting with Standing
      case _: Flying => new Shooting with Flying
    }
    copy(status = s) -> Bullet(this)
  }

  def update(implicit stage: Stage) = {
    val walked = if (properties.input.isControllerRight)
      walk(new Forward)
    else if (properties.input.isControllerLeft)
      walk(new Backward)
    else
      this
    val jumped = walked.status match {
      case s: Standing if properties.input.isButtonPressed(0) => walked.copy(status = s).jump
      case _ => walked
    }
    val (shooted, bullet) = if (properties.input.isButtonPressed(2))
      jumped.shoot.mapElements(identity, _.some)
    else
      jumped -> none
    val moved = shooted.status match {
      case m: Moving => shooted.copy(status = m).move
      case _ => shooted
    }
    val applied = moved.fix.fix.apply
    val d = stage.filteredObjects.any(applied.detect).fold[Player[_, _]](applied.damaged, applied)
    bullet.some(obj => Vector[GameplayObject](d, obj)).none(Vector[GameplayObject](d))
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
    new Player(new Normal with Standing, new Forward, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D[Float]], Life)
  }

}
