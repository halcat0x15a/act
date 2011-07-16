package baskingcat.act.gameplay

trait HasForm[A <: Form]

sealed trait Form

trait Standing extends Form

trait Flying extends Form
