package baskingcat.act.gameplay

import scala.xml._

import scalaz._
import Scalaz._

import baskingcat.act._

case class Stage(objects: GameplayObjects, size: Dimension[Float], viewport: Rectangle[Float])(implicit properties: GameProperties) {

  val gravity = 1.5f

  val friction = 0.5f

  val bounds = Rectangle(size)

  val effective = Rectangle(viewport.location - Vector2D(properties.size.width, properties.size.height), Dimension(properties.size.width * 3, properties.size.height * 3))

  val partitionedObjects = objects.partition(_.bounds.intersects(effective))

  val filteredObjects = partitionedObjects._1

  val blocks = filteredObjects.filter(_.isInstanceOf[Block])

  val player = objects.collect {
    case player: Player.Type => player
  }.headOption.err("Player not found")

}

object Stage {

  implicit def nodeToFloat(node: NodeSeq) = node.text.toFloat

  def apply(name: String)(implicit properties: GameProperties) = {
    val elem = XML.load(stream(name))
    val data = (elem \\ "rect").map { rect =>
      val x: Float = rect \ "@x"
      val y: Float = rect \ "@y"
      rect.attribute("id").map(_.text).map {
        case Player.Regex() => Seq(Player(x, y))
        case Enemy.Regex() => Seq(Enemy(x, y))
        case _ => for {
          i <- 0 until (rect \ "@width" / Block.Width).toInt
          j <- 0 until (rect \ "@height" / Block.Height).toInt
          block = Block(x |+| i * Block.Width, y |+| j * Block.Height)
        } yield block
      }
    }.flatten.flatten
    data.find(_.isInstanceOf[Player.Type]).map { player =>
      val y = player.bounds.bottom
      new Stage(Vector(data: _*), Dimension(elem \ "@width", elem \ "@height"), Rectangle(Point(0, y), properties.size))
    }.err("Player Not Found.")
  }

}
