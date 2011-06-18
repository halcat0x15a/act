package baskingcat.act.test

import org.scalacheck._

import baskingcat.act._
import baskingcat.act.gameplay._
import baskingcat.act.title._

object GameplayTest extends Properties("baskingcat.act.gameplay.Gameplay") {

  val gameplay = new Gameplay("stages/test.svg")(GameProperties(Dimension(800, 600), new TestController))

  property("logic") = Prop.forAll((i: Int) => !gameplay.logic.isInstanceOf[Title])

}

class TestController extends Controller {

  def isButtonPressed(n: Int): Boolean = false

  def isControllerRight = false
  def isControllerLeft = false
  def isControllerDown = false
  def isControllerUp = false

}
