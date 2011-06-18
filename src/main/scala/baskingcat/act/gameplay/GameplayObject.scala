package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

sealed abstract class GameplayObject[+A, +B] extends GameObject

trait Live[+A, +B] extends GameplayObject[A, B] {

  val life: Int

  def damaged(implicit stage: Stage): GameplayObject[A, B]

  def dead: Boolean = life <= 0 || bounds.top < 0

}

trait Movable[+A, +B] extends GameplayObject[A, B] {

  val velocity: Vector2f

  val direction: Symbol

  def move: GameplayObject[Moving, B]

}

trait Walkable[+A, +B] extends Movable[A, B] {

  def walk(implicit stage: Stage): GameplayObject[Walking, _ <: Direction]

}

trait Jumpable[+A, +B] extends Movable[A, B] {

  def jump(implicit stage: Stage): GameplayObject[Jumping, B]

}

case class Player[+A, +B](bounds: Rectangle, velocity: Vector2f, direction: Symbol, life: Int)(implicit properties: GameProperties) extends GameplayObject[A, B] with Live[A, B] with Walkable[A, B] with Jumpable[A, B] {

  def this(x: Float, y: Float)(implicit properties: GameProperties) = this(Rectangle(Vector2f(x, y), Dimension(Player.Width, Player.Height)), Vector2f(0, 0), 'right, Player.Life)

  lazy val name = 'miku

  def move = copy(bounds = bounds.copy(location = bounds.location + velocity))

  def walk(implicit stage: Stage) = {
    val vx = if (properties.controller.isControllerRight)
      velocity.x + Player.Speed
    else if (properties.controller.isControllerLeft)
      velocity.x - Player.Speed
    else
      velocity.x
    copy[Walking, Forward](velocity = velocity.copy(x = vx))
  }

  def jump(implicit stage: Stage) = {
    val vy = (properties.controller.isButtonPressed(0) && stage.bottomBlock(this).isDefined) ? -Player.JumpPower | velocity.y
    copy(velocity = velocity.copy(y = vy))
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

}

case class Enemy[+A, +B](bounds: Rectangle, velocity: Vector2f, direction: Symbol, life: Int) extends GameplayObject[A, B] with Live[A, B] with Movable[A, B] with Walkable[A, B] {

  def this(x: Float, y: Float) = this(Rectangle(Vector2f(x, y), Dimension(Player.Width, Player.Height)), Vector2f(0, 0), 'right, Enemy.Life)

  lazy val name = 'supu

  def move = copy(bounds = bounds.copy(location = bounds.location + velocity))

  def walk(implicit stage: Stage) = {
    copy(velocity = new Vector2f)
  }

  def detect(obj: GameplayObject[_, _]) = !obj.isInstanceOf[Block[_, _]] && !obj.isInstanceOf[Enemy[_, _]] && obj.bounds.intersects(bounds)

  def damaged(implicit stage: Stage) = copy(life = stage.objects.any(detect).fold(life - 1, life))

}

object Enemy {

  val Width: Float = 64

  val Height: Float = 64

  val Life: Int = 5

}

case class Block[+A, +B](bounds: Rectangle, velocity: Vector2f) extends GameplayObject[A, B] {

  def this(x: Float, y: Float) = this(Rectangle(Vector2f(x, y), Dimension(Block.Width, Block.Height)), Vector2f(0, 0))

  lazy val name = 'block

}

object Block {

  val Width: Float = 32

  val Height: Float = 32

}

case class Bullet[+A, +B](bounds: Rectangle, velocity: Vector2f, direction: Symbol, life: Int) extends GameplayObject[A, B] with Movable[A, B] with Live[A, B] {

  def this(obj: GameplayObject[_ <: State, _], direction: Symbol) = this(Rectangle(Vector2f((direction == 'right).fold(obj.bounds.right, obj.bounds.left - Bullet.Width), obj.bounds.top + obj.bounds.size.height / 2 - Bullet.Height / 2), Dimension(Bullet.Width, Bullet.Height)), Vector2f(10, 0), direction, 1)

  lazy val name = 'negi

  def move = copy(bounds = bounds.copy(location = bounds.location + velocity))

  def damaged(implicit stage: Stage): GameplayObject[A, B] = copy(life = stage.objects.any(obj => obj.bounds.intersects(bounds)).fold(life - 1, life))

}

object Bullet {

  val Width: Float = 16

  val Height: Float = 16

}
