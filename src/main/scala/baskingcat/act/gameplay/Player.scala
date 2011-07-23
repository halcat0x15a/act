package baskingcat.act.gameplay

import PartialFunction._

import scalaz._
import Scalaz._

import baskingcat.act._

abstract class Player extends GameObject

case class Miku[A <: Status, C <: Direction](name: Symbol, bounds: Rectangle, velocity: Vector2D, life: Int)(implicit properties: GameProperties, val status: Manifest[A], val direction: Manifest[C]) extends Player with Live[A] with Walkable[A, C] with Jumpable[A, C] with Shootable[A, C] {

  val obstacles = typeList[Cons[Enemy, Cons[Bullet, Nil]]]

  val speed: Float = 7f

  override val acceleration: Float = 1f

  val jumpPower: Float = 20f

  lazy val bullet = Bullet[C, Miku[A, C]](this)

  def movable(bounds: Rectangle): Miku[A, C] = copy(bounds = bounds.copy(location = bounds.location |+| velocity))

  def walkable[D <: Direction: Manifest](velocity: Vector2D): Miku[Walking, D] = copy[Walking, D](velocity = velocity)

  def jumpable(velocity: Vector2D): Miku[_ <: Jumping, C] = copy[Jumping, C](velocity = velocity)

  def shootable = copy[Shooting, C]()

  def live(velocity: Vector2D, life: Int): Miku[Damaging, C] = copy[Damaging, C](velocity = velocity, life = life)

  def fix(obj: GameObject)(implicit stage: Stage): Miku[A, C] = {
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
      fix(copy(bounds = bounds.copy(location = location)))
    else
      this
  }

  def apply(implicit stage: Stage): Miku[_ <: Status, C] = {
    val vx = if (lwalls.nonEmpty || rwalls.nonEmpty)
      0f
    else if (velocity.x > 0)
      velocity.x - stage.friction
    else if (velocity.x < 0)
      velocity.x |+| stage.friction
    else
      velocity.x
    val vy = (grounds.nonEmpty || (ceilings.nonEmpty && velocity.y < 0)) ? 0f | (velocity.y |+| stage.gravity)
    def cp[D <: Status: Manifest] = this.copy[D, C](velocity = Vector2D(vx, vy))
    if (grounds.nonEmpty)
      if (vx === 0)
        cp[Idling]
      else
        cp[Walking]
    else if (status <:< manifest[Jumping])
      cp[Jumping]
    else if (vx === 0)
      cp[Idling]
    else
      cp[Walking]
  }

  def update(implicit stage: Stage) = {
    type M = Miku[_, _]
    val walked = if (properties.input.isControllerRight && rwalls.isEmpty)
      walk[Forward]
    else if (properties.input.isControllerLeft && lwalls.isEmpty)
      walk[Backward]
    else
      this
    val jumped = (!(walked.asInstanceOf[M].status <:< manifest[Jumping]) && properties.input.isButtonPressed(0)).fold[GameObject](walked.asInstanceOf[M].jump, walked)
    val (shooted, bullet) = properties.input.isButtonPressed(1).fold[(GameObject, Option[Bullet])](jumped.asInstanceOf[M].shoot.mapElements(identity, Some.apply), jumped -> none)
    val applied = fix(shooted.asInstanceOf[M].move).apply
    val d = applied.alive
    bullet.some(obj => Vector[GameObject](d, obj)).none(Vector[GameObject](d))
  }

}

object Player {

  val Width = 64f

  val Height = 64f

  val Life = 1

  val JumpPower = 20f

  val Regex = """player.*""".r

  def apply(x: Float, y: Float)(implicit properties: GameProperties) = {
    new Miku[Idling, Forward]('miku, Rectangle(Point(x, y), Dimension(Width, Height)), mzero[Vector2D], Life)
  }

}
