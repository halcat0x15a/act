package baskingcat.act

import scalaz._
import Scalaz._

package object gameplay {

  type TypeList = List[Manifest[_]]

  def typeList[A <: ObjectTypeList: Manifest]: TypeList = {
    lazy val typeList: PartialFunction[TypeList, TypeList] = {
      case x :: xs => x :: typeList(xs.head.typeArguments)
      case _ => nil
    }
    typeList(manifest[A].typeArguments)
  }

}
