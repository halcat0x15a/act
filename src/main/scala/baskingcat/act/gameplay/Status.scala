package baskingcat.act.gameplay

import scalaz._
import Scalaz._

import baskingcat.act._

trait HasStatus[A <: GameObject with HasStatus[A]] { obj: GameObject =>

  val status: Status

  def status(status: Status): A

/*
  def statusSuffix = {
    val IM = manifest[Idling]
    val WM = manifest[Walking]
    val JWM = manifest[JWalking]
    val JM = manifest[Jumping]
    val ISM = manifest[IShooting]
    val WSM = manifest[WShooting]
    val JSM = manifest[JShooting]
    status match {
      case IM => "i"
      case WM => "w"
      case JWM => "jw"
      case JM => "j"
      case ISM => "is"
      case WSM => "ws"
      case JSM => "js"
    }
  }
*/
}

sealed trait Status

case object Idling extends Status

case object Jumping extends Status

case object Walking extends Status

case object Damaging extends Status

case object Shooting extends Status
