package baskingcat.act.gameplay

sealed abstract class State

sealed abstract class Init extends State

sealed abstract class Moving extends State

sealed abstract class Walking extends Moving

sealed abstract class Jumping extends Moving
