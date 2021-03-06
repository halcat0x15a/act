package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

case class Player[A <: Status, B <: Form, C <: Direction](name: Symbol, bounds: Rectangle[Float], velocity: Vector2D[Float], life: Int)(implicit properties: GameProperties, val status: Manifest[A], val form: Manifest[B], val direction: Manifest[C]) extends GameplayObject with Live[A] with Walkable[A, B, C] with Jumpable[A, B] with Shootable[A, C] {

  val speed: Float = 7f

  def move: Player[A, B, C] = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walk[D <: Direction: Manifest]: Player[_ <: Walking, B, D] = {
    def v(signum: Int) = (velocity.x |+| Player.Acceleration * signum) |> (vx => (vx.abs > speed).fold(speed * signum, vx))
    val vx = (manifest[C] <:< manifest[Forward]).fold(v(1), v(-1))
    copy[Walking, B, D](velocity = velocity.copy(x = vx))
  }

  def jump: Player[_ <: Jumping, _ <: Form, C] = copy[Jumping, Flying, C](velocity = velocity.copy(y = -Player.JumpPower))

  def fix(implicit stage: Stage): Player[A, B, C] = {
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
    lazy val x = if (rwalls.nonEmpty)
      rwallLeft - bounds.size.width
    else if (lwalls.nonEmpty)
      lwallRight
    else
      bounds.location.x
    lazy val y = if (grounds.size > 0)
      groundTop - bounds.size.height
    else if (ceilings.size > 0)
      ceilingBottom
    else
      bounds.location.y
    lazy val location = if (vmargin < hmargin)
      Point(bounds.location.x, y)
    else if (hmargin < vmargin)
      Point(x, bounds.location.y)
    else
      Point(x, y)
    if ((hmargin /== 0) && (vmargin /== 0))
      copy(bounds = bounds.copy(location = location)).fix
    else
      this
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

  def detect(obj: GameplayObject) = obj.bounds.intersects(bounds) && cond(obj) {
    case _: Enemy.Type => true
    case b: Bullet.Type if !b.owner.isInstanceOf[Player.Type] => true
  }

  def damaged: Player[_ <: Damaging, B, C] = copy[Damaging, B, C](velocity = -velocity, life = life - 1)

  def shoot = copy[Shooting, B, C]() -> Bullet(this)

  def update(implicit stage: Stage) = {
    val walked = if (properties.input.isControllerRight && rwalls.isEmpty)
      walk[Forward]
    else if (properties.input.isControllerLeft && lwalls.isEmpty)
      walk[Backward]
    else
      this
    val jumped = (walked.form <:< manifest[Standing] && properties.input.isButtonPressed(0)).fold[Player.Type](walked.jump, walked)
    val (shooted, bullet) = properties.input.isButtonPressed(1).fold[(Player.Type, Option[Bullet.Type])](jumped.shoot.mapElements(identity, _.some), jumped -> none)
    val applied = shooted.move.fix.apply
    val d = applied.live
    bullet.some(obj => Vector[GameplayObject](d, obj)).none(Vector[GameplayObject](d))
  }

}

object Player {

  type Type = Player[_ <: Status, _ <: Form, _ <: Direction]

  val Width = 64f

  val Height = 64f

  val Life = 1

  val Acceleration: Float = 1f

  val JumpPower = 20f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float)(implicit properties: GameProperties) = {
    new Player[Idling, Standing, Forward]('miku, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D[Float]], Life)
  }

}
