package baskingcat.act.gameplay

trait HasStatus[A <: Status]

sealed trait Status

trait Idling extends Status

trait Moving extends Status

trait Walking extends Moving

trait Jumping extends Moving

trait Damaging extends Status

trait Shooting extends Status
