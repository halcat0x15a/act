package baskingcat.act

package object gameplay {

  type GObj = GameplayObject[_ <: State, _ <: Direction]

  type GameplayObjects = Vector[GObj]

}
