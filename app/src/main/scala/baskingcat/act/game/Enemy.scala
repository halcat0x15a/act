package baskingcat.act.game

import baskingcat.act._
import util._

final case class Enemy(var x: Float, override var y: Float, override var direction: Direction.Value, override var life: Float)(override val id: String, override val textures: Textures, override val width: Float, override val height: Float, override val bulletTextures: Textures, override val bulletWidth: Float, override val bulletHeight: Float, val itemTextures: Textures) extends GameObject with HasDirection with Living with Damagable with Movable with Jumpable with Flyable with Shotable {

  override val fixed = false
  override var invincible = false
  override val speed = 1f
  override val power: Float = 15
  override val jumpPower = 0f
  val moveType = Enemy.MoveType.Line
  texImage = Random.nextInt(4)

  lazy val item = if (Random.nextInt(100) < 10) {
    Some(new Item(this))
  } else {
    None
  }

}

final object Enemy {

  object MoveType extends Enumeration {

    val Line = Value

  }

}
