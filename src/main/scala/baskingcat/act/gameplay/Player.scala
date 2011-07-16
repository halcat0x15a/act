package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: Status, B <: Form, C <: Direction](name: Symbol, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties, val action: Manifest[A], val status: Manifest[B], val direction: Manifest[C]) extends GameplayObject with Live[A] with Walkable[A, B, C] with Jumpable[A, B] with Shootable[A, C] {

  type P = Player[_ <: Status, _ <: Form, _ <: Direction]

  def move: Player[A, B, C] = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[D <: Direction: Manifest]: Player[_ <: Walking, B, D] = {
    val vx = (manifest[C] <:< manifest[Forward]).fold(velocity.x |+| Player.Speed, velocity.x - Player.Speed)
    copy[A with Walking, B, D](velocity = velocity.copy(x = vx))
  }

  def jump: Player[_ <: Jumping, _ <: Form, C] = copy[Jumping with A, Flying, C](velocity = velocity.copy(y = -Player.JumpPower))

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

  def apply(implicit stage: Stage): Player[_ <: Status, _ <: Form, C] = {
    val vx = if (lwalls.nonEmpty || rwalls.nonEmpty)
      0f
    else if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = (grounds.nonEmpty || (ceilings.nonEmpty && velocity.y < 0)) ? 0f | (velocity.y |+| stage.gravity)
    def cp[D <: Status: Manifest, E <: Form: Manifest] = this.copy[D, E, C](velocity = Vector2D(vx, vy))
    if (grounds.nonEmpty)
      if (vx === 0)
        cp[Idling, Standing]
      else
        cp[Walking, Standing]
    else if (status <:< manifest[Jumping])
      cp[Jumping, Flying]
    else if (vx === 0)
      cp[Idling, Flying]
    else
      cp[Walking, Flying]
  }

  def detect(obj: GameplayObject) = !obj.isInstanceOf[Block] && !obj.isInstanceOf[Player[_, _, _]] && obj.bounds.intersects(bounds)

  def damaged: Player[_ <: Damaging, B, C] = copy[A with Damaging, B, C](velocity = -velocity, life = life - 1)

  def shoot = copy[A with Shooting, B, C]() -> Bullet(this)

  def update(implicit stage: Stage) = {
    println(status)
    val walked = if (properties.input.isControllerRight)
      walk[Forward]
    else if (properties.input.isControllerLeft)
      walk[Backward]
    else
      this
    val jumped = (walked.status <:< manifest[Standing] && properties.input.isButtonPressed(0)).fold[P](walked.jump, walked)
    val (shooted, bullet) = properties.input.isButtonPressed(2).fold[(P, Option[Bullet[_, _, _]])](jumped.shoot.mapElements(identity, _.some), jumped -> none)
    val moved = (shooted.status <:< manifest[Moving]).fold[P](shooted.move, shooted)
    val applied = moved.fix.fix.apply
    val d = stage.filteredObjects.any(applied.detect).fold[P](applied.damaged, applied)
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
    new Player[Idling, Standing, Forward]('miku, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D[Float]], Life)
  }

}
