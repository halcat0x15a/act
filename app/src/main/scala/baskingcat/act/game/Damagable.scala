package baskingcat.act.game

trait Damagable extends GameObject {

  val power: Float

  def damage(living: Living) {
    if (!living.invincible) {
      living.life -= power
      if (living.isInstanceOf[Player]) {
        living.invincible = true
        living.alpha = 0.5f
        living.vx = (-living.vx + vx) / 2
      }
    }
  }

}
