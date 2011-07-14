package baskingcat.act.gameplay

trait HasDirection[A <: Direction] { obj: GameplayObject =>

  val direction: A

}
