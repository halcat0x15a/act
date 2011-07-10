package baskingcat.act.gameplay

sealed trait State

trait Moving extends State

trait Standing extends State

trait Flying extends State

class Normal extends Standing

class Walking extends Moving

class Jumping extends Moving with Flying

class Damaging extends State
