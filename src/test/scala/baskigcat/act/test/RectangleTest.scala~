package baskingcat.act.test

import org.scalacheck._

import baskingcat.act._

object RectangleTest extends Properties("baskingcat.act.Rectangle") {

  def check(f: Rectangle => Boolean) = Prop.forAll(Gen.posNum[Float], Gen.posNum[Float], Gen.posNum[Float], Gen.posNum[Float])((a, b, c, d) => f(new Rectangle(a, b, c, d)))

  property("contains") = check(rect => rect.contains(new Rectangle(rect.x + 0.1f, rect.y + 0.1f, rect.width - 0.2f, rect.height - 0.2f)))

  property("intersects") = check(rect => rect.intersects(new Rectangle(rect.x - 0.1f, rect.y - 0.1f, rect.width + 0.2f, rect.height + 0.2f)))

}
