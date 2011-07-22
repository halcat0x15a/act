package baskingcat.act.gameplay

trait HasForm[A <: Form] {

  val form: Manifest[A]

}

sealed trait Form

trait Standing extends Form

trait Flying extends Form
