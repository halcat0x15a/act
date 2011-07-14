package baskingcat.act.gameplay

sealed trait Status

trait Moving extends Status

trait Standing extends Status

trait Flying extends Status

class Normal extends Standing

class Walking extends Moving

class Jumping extends Moving with Flying

class Damaging extends Status

class Shooting extends Status
