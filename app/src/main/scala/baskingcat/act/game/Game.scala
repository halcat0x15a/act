package baskingcat.act.game

import com.baskingcat.game._
import baskingcat.act._
import clear.Clear
import title.Title
import actors.Actor._
import collection.immutable.HashSet
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

  def this(data: Set[GameObject]) = this(data, data, data map (_.right) max, new Timer)

  def this(stage: Elem) = this(Parser.parse(stage))

  def this(game: Game, entries: Set[GameObject]) = this(game.data, entries, game.goal, game.timer)

  override def logic(controller: GameController): Scene = {
    val n = System.nanoTime
    val blocks = get[Block](entries)
    val bullets = get[Shotable](entries) flatMap (_.bullets)
    val filteredNearObj = nearObj[Product with GameObject](data, getPlayer(entries)) filter (obj => !entries.exists(_.id == obj.id))
    for (obj <- entries ++ bullets) {
      // アクション
      obj match {
        case player: Player => {
          val ground = obj.findGround(blocks)
          if (controller.buttonPressed(5)) {
            return clear
          }
          val xAxis = controller.xAxis
          player.direction = if (xAxis > deadZone) {
            Direction.Right
          } else if (xAxis < -deadZone) {
            Direction.Left
          } else {
            player.direction
          }
          if (abs(xAxis) > deadZone)
            player.move(ground.isDefined)
          if (controller.buttonPressed(1)) {
            player.jump(ground.isDefined)
          }
          if (controller.buttonPressed(2)) {
            player.shot(Bullet.Type.Normal)
          }
        }
        case enemy: Enemy => {
          val ground = obj.findGround(blocks)
          if (enemy.vx == 0) {
            enemy.turn
          } else {
            ground foreach { ground =>
              enemy.direction = if (enemy.left <= ground.left) {
                Direction.Right
              } else if (enemy.right >= ground.right) {
                Direction.Left
              } else {
                enemy.direction
              }
            }
          }
          enemy.move(ground.isDefined)
        }
        case block: Block => {}
        case bullet: Bullet => {
          bullet.move(false)
        }
        case _ =>
      }

      obj match {
        case player: Player => {
          player.texImage = if (player.vy != 0) {
            if (player.direction == Direction.Right) {
              14
            } else {
              15
            }
          } else if (abs(player.vx.toInt) % 2 == 1) {
            if (player.texImage < 6) {
              player.texImage + 2
            } else {
              if (player.direction == Direction.Right) {
                0
              } else {
                1
              }
            }
          } else if (player.vx == 0 && player.texImage != 8 && player.texImage != 9) {
            if (player.direction == Direction.Right) {
              0
            } else {
              1
            }
          } else {
            player.texImage
          }
        }
        case _ =>
      }

      // 座標の更新
      obj.x += obj.vx
      obj.y += obj.vy

      val ground = obj.findGround(blocks)
      // 座標の修正
      if (ground.isDefined) {
        obj.vy = 0
      } else if (!obj.fixed) {
        obj.vy -= gravity
      }
      if (!obj.fixed) {
        ground foreach { ground =>
          if (obj.vx > 0) {
            obj.vx -= ground.friction
          } else if (obj.vx < 0) {
            obj.vx += ground.friction
          }
          if (abs(obj.vx) - ground.friction < 0) {
            obj.vx = 0
          }
        }
      }

      if (ground.isDefined && obj.vy <= 0) {
        obj.y = ground.get.top
        obj.vy = 0f
      }
      if (!obj.fixed) {
        def overlapsV(block: GameObject) = obj.top > block.bottom && obj.bottom < block.top
        blocks find (block => obj.right >= block.left && obj.right < block.right && obj.left < block.left && overlapsV(block)) foreach { block =>
          obj.x = block.left - obj.width
          obj.vx = 0f
        }
        blocks find (block => obj.left <= block.right && obj.left > block.left && obj.right > block.right && overlapsV(block)) foreach { block =>
          obj.x = block.right
          obj.vx = 0
        }
	if (obj.left < start) {
	  obj.x = start
	  obj.vx = 0
	} else if (!obj.isInstanceOf[Player] && obj.right > goal) {
	  obj.x = goal - obj.width
	  obj.vx = 0
	}
      }
      if (!obj.fixed) {
        blocks find (block => obj.top >= block.bottom && obj.bottom < block.bottom && obj.left < block.right && obj.right > block.left) foreach { block =>
          obj.y = block.bottom - obj.height - 1
          obj.vy = 0f
        }
      }

      // ダメージ
      obj match {
        case player: Player => if (player.alpha < 1) {
          player.alpha += 0.01f
        } else {
          player.invincible = false
        }
        case _ =>
      }
      obj match {
        case damagable: Damagable => {
          val filtered = entries filter (obj => obj.isInstanceOf[Living] && obj.id != damagable.id) filter (_ match {
            case bullet: Bullet => !obj.isInstanceOf[Bullet]
            case enemy: Enemy => !obj.isInstanceOf[Enemy]
            case _ => true
          })
          filtered find (damagable.intersects) map (_.asInstanceOf[Living]) foreach { damagable.damage }
        }
        case _ =>
      }
    }

    val player = getPlayer(entries)
    val updated = entries map (_ match {
      case enemy: Enemy if enemy.dead => enemy.item.getOrElse(enemy)
      case obj: GameObject => obj
    }) filter { obj =>
      if (obj.top > 0) {
        obj match {
          case living: Living => !living.dead
          case item: Item => if (player.intersects(item)) {
            player.get(item)
            false
          } else {
            true
          }
          case _ => true
        }
      } else {
        false
      }
    }
    updated foreach (_ match {
      case shotable: Shotable => {
        shotable.bullets = shotable.bullets filter { bullet =>
          val obstacles = entries filter (_.id != bullet.id)
          bullet.left < goal && bullet.right > start && !(obstacles exists (bullet.intersects))
        }
      }
      case _ =>
    })

    findPlayer(updated) match {
      case Some(player) => {
        if (player.left > goal) {
          clear
        } else {
          println((System.nanoTime - n).toFloat / 1000000)
          new Game(this, updated)
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
    glPushMatrix()
    glLoadIdentity()
    glMatrixMode(GL_MODELVIEW)
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
    get[Shotable](entries) flatMap (_.bullets) foreach (_.draw())
    entries foreach (_.draw())
    glPopMatrix()
    lifeGauge.draw()
  }

  def dispose() {
    //data foreach (_.textures.delete())
    //get[Shotable](data) foreach (_.bulletTextures.delete())
    //get[Enemy](data) foreach (_.itemTextures.delete())
    //entries foreach (_.delete())
  }

  def clear = {
    dispose()
    new Clear(timer.getTime)
  }

  def over = {
    dispose()
    new Title
  }

  private def get[T <: GameObject](entries: Set[GameObject])(implicit m: Manifest[T]): Set[T] = {
    val a = entries filter (entry => m.erasure.isInstance(entry))
    val b = data filter (d => m.erasure.isInstance(d) && !entries.contains(d.asInstanceOf[T]))
    (a ++ b) map (_.asInstanceOf[T])
  }

}

object Game {

  def findPlayer(data: Set[GameObject]) = data find (_.isInstanceOf[Player]) map (_.asInstanceOf[Player])

  def getPlayer(data: Set[GameObject]) = findPlayer(data).getOrElse(Message.error(new Exception, "プレーヤーが存在しません。"))

  def near(obj1: GameObject, obj2: GameObject) = {
    val width = ACT.width * 2
    obj1.left < obj2.hCenter + width && obj1.right > obj2.hCenter - width
  }

  def nearObj[T <: GameObject](data: Set[GameObject], obj: GameObject)(implicit m: ClassManifest[T]) = data filter (d => m.erasure.isInstance(d) && near(d, obj)) map (_.clone[T])

  val deadZone = 0.5f

  val gravity = 0.7f

  val resistance = 0.001f

  val start = 0f

}
