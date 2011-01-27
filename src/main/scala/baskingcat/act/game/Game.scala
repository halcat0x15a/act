package baskingcat.act.game

import com.baskingcat.game._
import baskingcat.act._
import clear.Clear
import title.Title
import actors.Actor._
import util.continuations._
import math._
import reflect.Manifest
import xml._
import org.lwjgl._
import org.lwjgl.opengl.GL11._
import org.lwjgl.util.Timer
import org.lwjgl.util.glu.GLU._

final class Game(private val data: Set[GameObject], entries: Set[GameObject], private val goal: Float, private val timer: Timer) extends Scene {

  import Game._

  def this(data: Set[GameObject]) = this(data, Game.nearObj[GameObject](data, Game.findPlayer(data)), data map (_.right) max, new Timer)

  def this(stage: Elem) = this(Parser.parse(stage))

  def this(game: Game, entries: Set[GameObject]) = this(game.data, entries, game.goal, game.timer)

  // Player(id, textures, x, y, width, height, vx, vy, direction, life)

  override def logic(controller: GameController): Scene = {
    val n = System.nanoTime
    val blockable = get[Blockable]
    val landable = get[Landable]
    val damagable = get[Damagable]
    val upadated: Set[GameObject] = {
      implicit def objToSet(obj: GameObject) = Set[GameObject](obj)
      (entries ++ nearObj[GameObject](data, findPlayer(entries)).filter(obj => !entries.exists(_.id == obj.id))) flatMap { obj =>
        val ground = obj.ground(landable)
        val onGround = ground.isDefined
        obj match {
          case player: Player => {
            if (controller.buttonPressed(5)) {
              return clear
            }
            val xAxis = controller.xAxis
            val (vx, direction) = if (xAxis > deadZone) {
              (player.move(Direction.Right, onGround), Direction.Right)
            } else if (xAxis < -deadZone) {
              (player.move(Direction.Left, onGround), Direction.Left)
            } else {
              (player.vx, player.direction)
            }
            val vy = if (controller.buttonPressed(1)) {
              player.jump(onGround)
            } else {
              player.vy
            }
            val bullets = if (controller.buttonPressed(2)) {
              player.shot(new Bullet(player, Bullet.Type.Normal))
            } else {
              null
            }
            val playerSet = new Player(player, player.x, player.y, vx, vy, direction, player.life)
            if (bullets != null) {
              playerSet ++ bullets
            } else {
              playerSet
            }
          }
          case enemy: Enemy => {
            val direction = if (enemy.vx == 0) {
              enemy.turn
            } else {
              ground match {
                case Some(ground) => {
                  if (enemy.left <= ground.left) {
                    Direction.Right
                  } else if (enemy.right >= ground.right) {
                    Direction.Left
                  } else {
                    enemy.direction
                  }
                }
                case None => enemy.direction
              }
            }
            val vx = enemy.move(direction, onGround)
            new Enemy(enemy, enemy.x, enemy.y, vx, enemy.vy, direction, enemy.life)
          }
          case block: Block => block
          case bullet: Bullet => {
            val vx = bullet.move(bullet.direction, false)
            new Bullet(bullet, bullet.x, bullet.y, vx, bullet.vy)
          }
        }
      }
    } map { obj =>

      val life: Float = if (!obj.invincible) {
        val d = obj match {
          case Player(id, _, _, _, _, _, _, _, _, _) => damagable filter (_.id != id)
          case enemy: Enemy => damagable filter (damagable => damagable.id != enemy.id && !damagable.isInstanceOf[Enemy])
        }
        obj.damaged(d)
      } else {
        0f
      }

      val x = obj.x + obj.vx

      val y = obj.y + obj.vy

      val ground = obj.ground(landable)
      val onGround = ground.isDefined

      val vx: Float = if (!obj.isInstanceOf[Fixing]) {
        ground match {
          case Some(ground) => {
            val vx = if (obj.vx > 0) {
              obj.vx - ground.friction
            } else if (obj.vx < 0) {
              obj.vx + ground.friction
            } else {
              0
            }
            if (abs(vx) - ground.friction < 0) {
              0
            } else {
              vx
            }
          }
          case None => obj.vx
        }
      } else {
        obj.vx
      }

      val vy = if (!obj.isInstanceOf[Fixing]) {
        if (!onGround) {
          obj.vy - gravity
        } else {
          obj.vy
        }
      } else {
        obj.vy
      }

      obj match {
        case Player(id, textures, _, _, width, height, _, _, direction, _) => Player(id, textures, x, y, width, height, vx, vy, direction, life)
        case Enemy(id, textures, _, _, width, height, _, _, direction, _) => Enemy(id, textures, x, y, width, height, vx, vy, direction, life)
        case bullet: Bullet => new Bullet(bullet, x, y, vx, vy)
        case block: Block => block
      }

    } map { obj =>

      val ground = obj.ground(landable)
      val onGround = ground != null

      val (y, vy) = if (!obj.isInstanceOf[Fixing]) {
        blockable find { block => obj.top >= block.bottom && obj.bottom < block.bottom && obj.left < block.right && obj.right > block.left } match {
          case Some(block) => {
            (block.bottom - obj.height - 1, 0f)
          }
          case None => {
            if (ground.isDefined && obj.vy <= 0) {
              (ground.get.top, 0f)
            } else {
              (obj.y, obj.vy)
            }
          }
        }
      } else {
        (obj.y, obj.vy)
      }

      val (x, vx): (Float, Float) = {
        val top = y + obj.height
        val bottom = y
        val walls = if (ground.isInstanceOf[Blockable]) {
          blockable - ground.asInstanceOf[Blockable]
        } else {
          blockable
        }
        def overBlockable = {
          def overlapsV(wall: GameObject) = top > wall.bottom && bottom < wall.top
          walls find (wall => obj.right >= wall.left && obj.right < wall.right && obj.left < wall.left && bottom != wall.top && overlapsV(wall)) match {
            case Some(wall) => {
              (wall.left - obj.width, 0f)
            }
            case None => {
              walls find (wall => obj.left <= wall.right && obj.left > wall.left && obj.right > wall.right && bottom != wall.top && overlapsV(wall)) match {
                case Some(wall) => {
                  (wall.right, 0f)
                }
                case None => {
                  (obj.x, obj.vx)
                }
              }
            }
          }
        }
        obj match {
          case player: Player => {
            if (player.x < start) {
              (start, 0f)
            } else {
              overBlockable
            }
          }
          case _ => overBlockable
        }
      }

      obj match {
        case Player(id, textures, _, _, width, height, _, _, direction, life) => Player(id, textures, x, y, width, height, vx, vy, direction, life)
        case enemy: Enemy => new Enemy(enemy, x, y, vx, vy, enemy.direction, enemy.life)
        case bullet: Bullet => new Bullet(bullet, x, y, vx, vy)
        case block: Block => block
      }

    } filter { obj =>
      val player = findPlayer(entries)
      if (obj.top > 0 && near(obj, player)) {
        obj match {
          case bullet: Bullet => { //更新されていないオブジェクト
            val obstacles = entries filter (fixed => !fixed.isInstanceOf[Bullet] && fixed.id != bullet.id)
            bullet.left < goal && bullet.right > start && !(obstacles exists (bullet.hit(_)))
          }
          case _ => {
            if (obj.invincible) {
              true
            } else {
              obj.life > 0
            }
          }
        }
      } else {
        false
      }
    }
    locally {
      val player = findPlayer(upadated)
      if (player == null) {
        over
      } else if (player.left > goal) {
        clear
      } else {
        println((System.nanoTime - n).toFloat / 100000000)
        new Game(this, upadated)
      }
    }
  }

  def render() {
    actor {

    }
    glClear(GL_COLOR_BUFFER_BIT)
    glPushMatrix()
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    val hCenter = findPlayer(entries).hCenter
    val x = if (hCenter <= ACT.halfWidth) {
      0
    } else if (hCenter >= goal - ACT.halfWidth) {
      goal - ACT.width
    } else {
      hCenter - ACT.halfWidth
    }
    val y = 0f
    gluLookAt(x, y, 1, x, y, 0, 0, 1, 0)
    for (entry <- entries) {
      entry.draw()
    }
    glPopMatrix()
  }

  def clear = {
    new Clear(timer.getTime)
  }

  def over = {
    new Title
  }

  private def get[T <: GameObject](entries: Set[GameObject] = this.entries)(implicit m: Manifest[T]): Set[T] = {
    val a = entries filter (entry => m.erasure.isInstance(entry))
    val b = data filter (d => m.erasure.isInstance(d) && !entries.contains(d.asInstanceOf[T]))
    (a ++ b) map (_.asInstanceOf[T])
  }

  private def get[T <: GameObject](implicit m: Manifest[T]): Set[T] = get[T](entries)

}

object Game {

  def findPlayer(data: Set[GameObject]) = data find (d => d.isInstanceOf[Player]) match {
    case Some(player) => player.asInstanceOf[Player]
    case None => null
  }

  def near(obj1: GameObject, obj2: GameObject) = obj1.left < obj2.hCenter + ACT.width && obj1.right > obj2.hCenter - ACT.width

  def nearObj[T <: GameObject](data: Set[GameObject], obj: GameObject)(implicit m: ClassManifest[T]) = data filter (d => m.erasure.isInstance(d) && near(d, obj)) map (d => d.asInstanceOf[T])

  val deadZone = 0.5f

  val gravity = 0.7f

  val resistance = 0.001f

  val start = 0f

}
