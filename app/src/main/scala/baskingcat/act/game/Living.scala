package baskingcat.act.game

trait Living extends GameObject {

  var invincible: Boolean

  var life: Float

  def dead = life <= 0

}
