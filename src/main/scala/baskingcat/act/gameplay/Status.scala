package baskingcat.act.gameplay

sealed trait Status

trait Moving extends Status

trait Standing extends Status

trait Flying extends Status

trait Normal extends Standing

trait Walking extends Moving

trait Jumping extends Moving with Flying

trait Damaging extends Status

trait Shooting extends Status
