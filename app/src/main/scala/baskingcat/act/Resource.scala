package baskingcat.act

import com.baskingcat.game._
import baskingcat.act._
import java.awt.image._
import java.io._
import javax.imageio._
import xml._

object Resource {

  val cl = getClass.getClassLoader

  implicit def loadImage(name: String) = {
    try {
      Array(ImageIO.read(cl.getResource(name)))
    } catch {
      case e => Message.fileNotFoundError(e, name)
    }
  }

  implicit def loadImages(names: Seq[String]) = names flatMap (loadImage(_)) toArray

  lazy val config = try {
    XML.load(cl.getResource("config.xml"))
  } catch {
    case e: FileNotFoundException => Message.fileNotFoundError(e, "config.xml")
  }

  lazy val stages = try {
    List(XML.load(cl.getResource("stages/stage.svg")))
  } catch {
    case e: FileNotFoundException => Message.fileNotFoundError(e, "config.xml")
  }

  lazy val textures = Map(
    "title" -> Textures.load("backgrounds/title.png", false),
    "background" -> Textures.load("backgrounds/background.png", false),
    "clear" -> Textures.load("backgrounds/clear.png", false))

  def destroy() {
    println("ぬるぽ")
    textures.values foreach { textures =>
      try {
        textures.delete()
      } catch {
        case e: NullPointerException => println("ｶﾞｯ")
      }
    }
  }

  def negi = Textures.load("items/negi.png", false)

  def miku = Textures.load(1 to 16 map ("miku/miku" + _ + ".png"), false)

  def supu = Textures.load(List("", "r", "g", "b") map ("supu/supu" + _ + ".png"), false)

  def block = Textures.load("blocks/block.png", true)

}
