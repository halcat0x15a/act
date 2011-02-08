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

final class Game(private val data: Set[Product with GameObject], entries: Set[Product with GameObject], private val goal: Float, private val timer: Timer) extends Scene {

  import Game._

  def this(data: Set[Product with GameObject]) = this(data, Game.nearObj[Product with GameObject](data, Game.getPlayer(data)), data map (_.right) max, new Timer)

  def this(stage: Elem) = this(Parser.parse(stage))

  def this(game: Game, entries: Set[Product with GameObject]) = this(game.data, entries, game.goal, game.timer)

  override def logic(controller: GameController): Scene = {
    val n = System.nanoTime
    val blockables = get[Blockable](entries)
    val landables = get[Landable](entries)
    val filteredNearObj = nearObj[Product with GameObject](data, getPlayer(entries)) filter (obj => !entries.exists(_.id == obj.id))
    val acted = (entries ++ filteredNearObj) flatMap { obj =>
      implicit def obj2Set(obj: Product with GameObject) = Set(obj)
      obj match {
        case player: Player => {
          val ground = player.findGround(landables)
          if (controller.buttonPressed(5)) {
            return clear
          }
          val xAxis = controller.xAxis
          val (vx, direction) = if (xAxis > deadZone) {
            (player.move(Direction.Right, ground.isDefined), Direction.Right)
          } else if (xAxis < -deadZone) {
            (player.move(Direction.Left, ground.isDefined), Direction.Left)
          } else {
            (player.vx, player.direction)
          }
          val vy = if (controller.buttonPressed(1)) {
            player.jump(ground.isDefined)
          } else {
            player.vy
          }
          val bullets = if (controller.buttonPressed(2)) {
            Some(player.shot(Bullet.Type.Normal))
          } else {
            None
          }
          val _player = new Player(player, player.x, player.y, vx, vy, direction, player.life)
          bullets match {
            case Some(bullets) => _player ++ bullets
            case None => _player
          }
        }
        case enemy: Enemy => {
          val ground = enemy.findGround(landables)
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
          val vx = enemy.move(direction, ground.isDefined)
          new Enemy(enemy, enemy.x, enemy.y, vx, enemy.vy, direction, enemy.life)
        }
        case block: Block => {
          block
        }
        case Bullet(x, y, _, vy, direction) => {
	  val bullet = obj.asInstanceOf[Bullet]
          val vx = bullet.move(bullet.direction, false)
          new Bullet(bullet, x, y, vx, vy, direction)
        }
        case obj: GameObject => obj
      }
    }

    // 座標の更新
    val upadated: Set[Product with GameObject] = acted map { obj =>
      // 座標の移動
      val x = obj.x + obj.vx
      val y = obj.y + obj.vy
      val vx: Float = obj match {
        case fixing: Fixing => obj.vx
        case _ => {
          obj.findGround(landables) match {
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
        }
      }
      val vy = obj match {
        case fixing: Fixing => obj.vy
        case _ => {
          if (!obj.findGround(landables).isDefined) {
            obj.vy - gravity
          } else {
            obj.vy
          }
        }
      }
      obj match {
        case Player(_, _, _, _, direction, life) => new Player(obj.asInstanceOf[Player], x, y, vx, vy, direction, life)
        case Enemy(_, _, _, _, direction, life) => new Enemy(obj.asInstanceOf[Enemy], x, y, vx, vy, direction, life)
        case Bullet(_, _, _, _, direction) => new Bullet(obj.asInstanceOf[Bullet], x, y, vx, vy, direction)
        case Block(_, _, _, _) => new Block(obj.asInstanceOf[Block], x, y, vx, vy)
        case obj: GameObject => obj
      }
    } map { obj =>
      // 座標の修正
      val (y, vy) = obj match {
        case fixing: Fixing => (obj.y, obj.vy)
        case _ => {
          blockables find { block => obj.top >= block.bottom && obj.bottom < block.bottom && obj.left < block.right && obj.right > block.left } match {
            case Some(block) => {
              (block.bottom - obj.height - 1, 0f)
            }
            case None => {
              val ground = obj.findGround(landables)
              if (ground.isDefined && obj.vy <= 0) {
                (ground.get.top, 0f)
              } else {
                (obj.y, obj.vy)
              }
            }
          }
        }
      }
      val (x, vx): (Float, Float) = {
        val top = y + obj.height
        val bottom = y
        val blocks = obj.findGround(landables) match {
          case Some(block: Blockable) => blockables - block
          case None => blockables
        }
        def overBlockable = {
          def overlapsV(block: GameObject) = top > block.bottom && bottom < block.top
          blocks find (block => obj.right >= block.left && obj.right < block.right && obj.left < block.left && bottom != block.top && overlapsV(block)) match {
            case Some(block) => {
              (block.left - obj.width, 0f)
            }
            case None => {
              blocks find (block => obj.left <= block.right && obj.left > block.left && obj.right > block.right && bottom != block.top && overlapsV(block)) match {
                case Some(block) => {
                  (block.right, 0f)
                }
                case None => {
                  (obj.x, obj.vx)
                }
              }
            }
          }
        }
        obj match {
          case Player(x, _, _, _, _, _) => {
            if (x < start) {
              (start, 0f)
            } else {
              overBlockable
            }
          }
          case _ => overBlockable
        }
      }
      obj match {
        case Player(_, _, _, _, direction, life) => new Player(obj.asInstanceOf[Player], x, y, vx, vy, direction, life)
        case Enemy(_, _, _, _, direction, life) => new Enemy(obj.asInstanceOf[Enemy], x, y, vx, vy, direction, life)
        case Bullet(_, _, _, _, direction) => new Bullet(obj.asInstanceOf[Bullet], x, y, vx, vy, direction)
        case Block(x, y, vx, vy) => new Block(obj.asInstanceOf[Block], x, y, vx, vy)
        case obj: GameObject => obj
      }
    }

    val player = getPlayer(upadated)
    val damagables = get[Damagable](upadated)
    val filtered = upadated map (_ match {
      case living: Living => {
        val life = living.damaged(damagables filter (_.id != living.id))
        living match {
          case Player(x, y, vx, vy, direction, _) => new Player(living.asInstanceOf[Player], x, y, vx, vy, direction, life)
          case Enemy(x, y, vx, vy, direction, _) => new Enemy(living.asInstanceOf[Enemy], x, y, vx, vy, direction, life)
        }
      }
      case obj: GameObject => obj
    }) map (_ match {
      case enemy: Enemy if enemy.dead => enemy.item match {
        case Some(item) => item
        case None => enemy
      }
      case obj: GameObject => obj
    }) filter { obj =>
      if (obj.top > 0) {
        obj match {
          case bullet: Bullet => {
            val obstacles = upadated filter (_.id != bullet.id)
            bullet.left < goal && bullet.right > start && !(obstacles exists (bullet.intersects))
          }
          case living: Living => !living.dead
          case _ => true
        }
      } else {
        false
      }
    }

    findPlayer(filtered) match {
      case Some(player) => {
        if (player.left > goal) {
          clear
        } else {
          println((System.nanoTime - n).toFloat / 1000000)
          new Game(this, filtered)
        }
      }
      case None => over
    }
  }

  def render() {
    val background = new Background(Resource.textures("background"))
    val lifeGauge = new LifeGauge(findPlayer(entries) match {
      case Some(player) => player.life
      case None => 0
    })
    glClear(GL_COLOR_BUFFER_BIT)
    background.draw()
    glMatrixMode(GL_MODELVIEW)
    glLoadIdentity()
    glPushMatrix()
    findPlayer(entries) foreach { player =>
      val x = if (player.hCenter <= ACT.halfWidth) {
        0
      } else if (player.hCenter >= goal - ACT.halfWidth) {
        goal - ACT.width
      } else {
        player.hCenter - ACT.halfWidth
      }
      val y = if (player.vCenter <= ACT.halfHeight) {
        0
      } else if (player.vCenter >= goal - ACT.halfHeight) {
        // goal -> 天井
        goal - ACT.height
      } else {
        player.vCenter - ACT.halfHeight
      }
      gluLookAt(x, y, 1, x, y, 0, 0, 1, 0)
    }
    entries foreach (_.draw())
    glPopMatrix()
    lifeGauge.draw()
    entries foreach (_.delete())
    lifeGauge.delete()
    background.delete()
  }

  def dispose() {
    data foreach (_.textures.delete())
    get[Shotable](data) foreach (_.bulletTextures.delete())
    get[Enemy](data) foreach (_.itemTextures.delete())
    entries foreach (_.delete())
  }

  def clear = {
    dispose()
    new Clear(timer.getTime)
  }

  def over = {
    dispose()
    new Title
  }

  private def get[T <: Product with GameObject](entries: Set[Product with GameObject] = this.entries)(implicit m: Manifest[T]): Set[T] = {
    val a = entries filter (entry => m.erasure.isInstance(entry))
    val b = data filter (d => m.erasure.isInstance(d) && !entries.contains(d.asInstanceOf[T]))
    (a ++ b) map (_.asInstanceOf[T])
  }

}

object Game {

  def findPlayer(data: Set[Product with GameObject]) = data find (_.isInstanceOf[Player]) map (_.asInstanceOf[Player])

  def getPlayer(data: Set[Product with GameObject]) = {
    findPlayer(data) match {
      case Some(player) => player
      case None => {
        throw new Exception
      }
    }
  }

  def near(obj1: GameObject, obj2: GameObject) = {
    val width = ACT.width * 2
    obj1.left < obj2.hCenter + width && obj1.right > obj2.hCenter - width
  }

  def nearObj[T <: Product with GameObject](data: Set[Product with GameObject], obj: GameObject)(implicit m: ClassManifest[T]) = data filter (d => m.erasure.isInstance(d) && near(d, obj)) map (d => d.asInstanceOf[T])

  val deadZone = 0.5f

  val gravity = 0.7f

  val resistance = 0.001f

  val start = 0f

}
