package baskingcat.act.game

import baskingcat.act._
import com.baskingcat.game._
import xml._

object Parser {

  val floatPattern = """-?\d+(\.\d+)?""".r

  val translatePattern = ("""translate\(""" + floatPattern + """,\s*""" + floatPattern + """\)""").r

  implicit def nodeToString(node: NodeSeq) = node.text

  def parse(elem: Elem): Set[GameObject] = {
    val ratio = ACT.height / (elem \ "@height").text.toFloat
    implicit def nodeToFixedFloat(node: NodeSeq) = node.text.toFloat * ratio
    def translate(node: NodeSeq): (Float, Float) = translatePattern findFirstIn node \ "@transform" map (str => (floatPattern findAllIn str).toList map (_.toFloat * ratio)) map (list => (list(0), -list(1))) getOrElse((0, 0))
    val stageHeight = elem \ "@height"
    val rects = elem \\ "rect"
    val player = rects find (contains("#00FF00", "lime")) map { node =>
      val height = node \ "@height"
      val (x, y) = translate(node)
      Player(node \ "@x" + x, stageHeight - node \ "@y" - height + y)(node \ "@id", Resource.miku, node \ "@width", height, Resource.negi, 64, 64)
    }
    if (player.isEmpty)
      return Message.error(new Exception, "プレーヤーが存在しません")
    val enemies = rects filter (contains("#FF0000", "red")) map { node =>
      val height = node \ "@height"
      val (x, y) = translate(node)
      Enemy(node \ "@x" + x, stageHeight - node \ "@y" - height + y, Direction.Left, 70)(node \ "@id", Resource.supu, node \ "@width", height, Resource.negi, 0, 0, Resource.negi)
    }
    val blocks = rects filter (contains("#000000", "black")) map { node =>
      val height = node \ "@height"
      val (x, y) = translate(node)
      Block(node \ "@x" + x, elem \ "@height" - node \ "@y" - height + y)(node \ "@id", Block.Type.Normal, Resource.block, node \ "@width", height)
    }
    Set(player.get) ++ enemies ++ blocks
  }

  def contains(colors: String*)(node: NodeSeq) = {
    val color = node \ "@fill"
    val style = node \ "@style"
    colors exists (c => c == color || style.trim.contains("fill:" + c))
  }

}

