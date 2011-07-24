package baskingcat.act.gameplay

import scalaz._
import Scalaz._

trait HasStatus[A <: Status] {

  implicit val status: Manifest[A]

  def statusSuffix[A <: Status](implicit m: Manifest[A]) = if (m <:< manifest[Idling])
    "i"
  else if (m <:< manifest[Walking])
    "w"
  else if (m <:< manifest[Jumping])
    "j"
  else 
    undefined

}

sealed trait Status

trait Idling extends Status

trait Moving extends Status

trait Walking extends Moving

trait Jumping extends Moving

trait Damaging extends Status

trait Shooting extends Status

trait WShooting extends Shooting with Walking

trait JShooting extends Shooting with Jumping
