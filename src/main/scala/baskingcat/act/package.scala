package baskingcat

package object act {

  type Point = Vector2f

  def stream(name: String) = getClass.getClassLoader.getResourceAsStream(name)

}
