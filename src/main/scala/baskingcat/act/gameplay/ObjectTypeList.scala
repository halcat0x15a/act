package baskingcat.act.gameplay

import baskingcat.act._

trait ObjectTypeList

trait Cons[X <: GameObject, XS <: ObjectTypeList] extends ObjectTypeList

trait Nil extends ObjectTypeList
