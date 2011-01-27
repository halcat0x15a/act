package baskingcat.act.game

import baskingcat.act._
import xml._

object Parser {

    implicit def nodeToInt(elem: NodeSeq) = elem.text.toInt
    implicit def nodeToFloat(elem: NodeSeq) = elem.text.toFloat

  def parse(elem: Elem): Set[GameObject] = {
    //val ratio = elem \ "@height" / ACT.height
    val defs = elem \ "defs"
    val images = defs \ "images"
    val imagesMap: Map[String, Textures] = images map (e => ((e \ "@id").text -> nodeToTextures(e))) toMap
    val dataList = elem \ "data"
    def typeFilter(_type: String) = dataList filter (data => (data \ "@type").text == _type)
    implicit def imagesId(node: NodeSeq) = {
      val text = node.text
      text.substring(text.indexOf("#") + 1)
    }
    val blocks = typeFilter("block") map (e => new Block(e \ "@id", Block.Type.Normal, imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height"))
    val enemies = typeFilter("enemy") map (e => new Enemy(e \ "@id", imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height", 5))
    val player = typeFilter("player") map (e => new Player(e \ "@id", imagesMap(e \ "@images"), e \ "@x", e \ "@y", e \ "@width", e \ "@height"))
    (blocks ++ enemies ++ player).toSet
  }

  private def nodeToTextures(images: Node): Textures = {
    Textures.load(images \ "image" map (_.text.split(",") map (_.toByte)) toArray, images \ "@width", images \ "@height", (images \ "@repeat").text.toBoolean)
  }

}
