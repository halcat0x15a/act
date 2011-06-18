package baskingcat.act.gameplay

import scala.xml._

import scalaz._
import Scalaz._

import baskingcat.act._

case class Stage(objects: GameplayObjects, size: Dimension, viewport: Rectangle)(implicit properties: GameProperties) {

  def this(objects: GameplayObjects, size: Dimension)(implicit properties: GameProperties) = this(objects, size, new Rectangle(properties.size))

  val effective = Rectangle(viewport.location - Vector2f(properties.size.width, properties.size.height), Dimension(properties.size.width * 3, properties.size.height * 3))

  val filteredObjects = objects.filter(_.bounds.intersects(effective))

  val blocks = filteredObjects.filter(_.isInstanceOf[Block[_]])

  val player = objects.collect {
    case player: Player[_] => player
  }.headOption.err("Player not found")

  def bottomBlock(obj: GameplayObject[_]) = blocks.find(block => obj.bounds.top == player.bounds.bottom && block.bounds.left < obj.bounds.right && block.bounds.right < obj.bounds.left)

}

object Stage {

  private val PlayerRegex = """player.*""".r

  private val EnemyRegex = """enemy.*""".r

  implicit def nodeToFloat(node: NodeSeq): Float = node.text.toFloat

  def apply(name: String)(implicit properties: GameProperties): Stage = {
    val elem = XML.load(stream(name))
    val data = (elem \\ "rect").map { rect =>
      val x: Float = rect \ "@x"
      val y: Float = rect \ "@y"
      rect.attribute("id").map(_.text).map {
        case PlayerRegex() => Seq(new Player[Init](x, y))
        case EnemyRegex() => Seq(new Enemy[Init](x, y))
        case _ => for {
          i <- 0 until (rect \ "@width" / Block.Width).toInt
          j <- 0 until (rect \ "@height" / Block.Height).toInt
          block = new Block[Init](x + i * Block.Width, y + j * Block.Height)
        } yield block
      }
    }.collect {
      case Some(seq) => seq
    }.flatten
    data.find(_.isInstanceOf[Player[_ <: State]]).map { player =>
      val y = player.bounds.bottom
      new Stage(Vector(data: _*), Dimension(elem \ "@width", elem \ "@height"), Rectangle(Vector2f(0, y), properties.size))
    }.err("Player Not Found.")
  }

}
