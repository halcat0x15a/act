package baskingcat.act

abstract class Input {

  def isButtonPressed(n: Int): Boolean

  def isControllerUp: Boolean

  def isControllerDown: Boolean

  def isControllerLeft: Boolean

  def isControllerRight: Boolean

}
