package baskingcat.act

package object gameplay {

  type TypeList = List[Manifest[_]]

  def typeList[A <: ObjectTypeList: Manifest]: TypeList = {
    lazy val typeList: PartialFunction[TypeList, TypeList] = {
      case x :: xs => x :: typeList(xs.head.typeArguments)
      case _ => scala.Nil
    }
    typeList(manifest[A].typeArguments)
  }

}
