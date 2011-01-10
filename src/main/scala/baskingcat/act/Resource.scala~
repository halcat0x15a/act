package baskingcat.act

import com.baskingcat.game._
import baskingcat.act._
import java.awt.image._
import java.io._
import javax.imageio._
import xml._

object Resource {

  private val path = "/home/halcat/projects/act/src/main/resources/"

  implicit def loadImage(name: String) = {
    val path = new StringBuilder(this.path).append(name).toString
    try {
      ImageIO.read(new File(path))
    } catch {
      case e => throw e
    }
  }

  implicit def loadImages(names: Seq[String]) = names map (loadImage(_)) toArray

  lazy val config = XML.loadFile(path + "config.xml")

  lazy val stages = List(XML.loadFile(path + "stages/stage.xml"))

  lazy val imageDataMap = Map(
    "miku" -> new ImageData(1 to 14 map (new StringBuilder("miku/miku").append(_).append(".png").toString), false),
  "supu" -> new ImageData(List("", "r", "g", "b") map (new StringBuilder("supu/supu").append(_).append(".png").toString), false),
    "block" -> new ImageData("blocks/block.png", true),
    "negi" -> new ImageData("items/negi.png", false),
    "maiku" -> new ImageData("items/maiku.png", false),
    "clear" -> new ImageData("backgrounds/clear.png", false),
    "clear" -> new ImageData("backgrounds/clear.png", false),
    "clear" -> new ImageData("backgrounds/clear.png", false),
    "clear" -> new ImageData("backgrounds/clear.png", false),
    "clear" -> new ImageData("backgrounds/clear.png", false)
)

  lazy val textures = Map(
    "title" -> Texture.load(path + "backgrounds/title.png")
)

}
