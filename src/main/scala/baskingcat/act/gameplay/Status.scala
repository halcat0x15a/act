package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasStatus[A <: Status] {

  implicit val status: Manifest[A]

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

}

sealed trait Status

trait Idling extends Status

trait Moving extends Status

trait Jumping extends Moving

trait Walking extends Moving

trait JWalking extends Walking with Jumping

trait Damaging extends Status

trait Shooting extends Status

trait IShooting extends Shooting with Idling

trait WShooting extends Shooting with Walking

trait JShooting extends Shooting with Jumping
