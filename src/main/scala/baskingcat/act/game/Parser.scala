package baskingcat.act.game

import baskingcat.act._
import xml._

object Parser {

  def parse(elem: Elem): Set[GameObject] = {
    implicit def nodeToInt(elem: NodeSeq) = elem.text.toInt
    implicit def nodeToFloat(elem: NodeSeq) = elem.text.toFloat
    //val ratio = elem \ "@height" / ACT.height
    val defs = elem \ "defs"
    val images = defs \ "images"
    val imagesMap = images flatMap (e => Map((e \ "@id").text -> nodeToTextures(e))) toMap
    val dataList = elem \ "data"
    def typeFilter(_type: String) = dataList filter (data => (data \ "@type").text == _type)
    implicit def imagesId(node: NodeSeq) = {
      val text = node.text
      text.substring(text.indexOf("#") + 1, text.lastIndexOf(")"))
    }
    val blocks = typeFilter("block") map (e => new Block(e \ "@id", Block.Type.Normal, imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height"))
    val enemies = typeFilter("enemy") map (e => new Enemy(e \ "@id", imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height", 5))
    val player = typeFilter("player") map (e => new Player(e \ "@id", imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height"))
    (blocks ++ enemies ++ player).toSet
  }

  private def nodeToTextures(images: Node) = {
    implicit def nodeToInt(elem: NodeSeq) = elem.text.toInt
    images \ "image" map (image => Texture.load(image.text.split(",") map (_.toByte), images \ "@width", images \ "@height", (images \ "@repeat").text.toBoolean)) toArray
  }

}
