package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasStatus[A <: Status] {

  implicit val status: Manifest[A]

  def statusSuffix = status match {
    case Idling.Manifest => "i"
    case Walking.Manifest => "w"
    case Jumping.Manifest => "j"
    case IShooting.Manifest => "is"
    case WShooting.Manifest => "ws"
    case JShooting.Manifest => "js"
  }

}

sealed trait Status

trait Idling extends Status

object Idling {
  val Manifest = manifest[Idling]
}

trait Moving extends Status

trait Walking extends Moving

object Walking extends  {
  val Manifest = manifest[Walking]
}

trait Jumping extends Moving

object Jumping {
  val Manifest = manifest[Jumping]
}

trait Damaging extends Status

object Damaging {
  val Manifest = manifest[Damaging]
}

trait Shooting[A <: Status] extends Status

object IShooting {
  val Manifest = manifest[Shooting[Idling]]
}

object WShooting {
  val Manifest = manifest[Shooting[Jumping]]
}

object JShooting {
  val Manifest = manifest[Shooting[Jumping]]
}
