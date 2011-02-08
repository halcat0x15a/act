package baskingcat.act.editor

import baskingcat.act._
import java.awt.{ Color, TexturePaint }
import java.awt.geom._
import java.awt.image._
import java.io.{ File, PrintWriter }
import javax.swing.{ WindowConstants }
import actors.Actor._
import collection._
import math._
import swing._
import event._
import xml._

object ACTEditor extends SimpleSwingApplication {

  val width = 800
  val height = 600
  val maxWidth = 100000
  val maxHeight = 10000
  val gridSize = 32
  val vLine = (width / gridSize).toInt
  val hLine = (height / gridSize).toInt
  val dataSet = mutable.LinkedHashSet.empty[Data]
  val fileChooser = {
    val fileChooser = new FileChooser
    fileChooser.multiSelectionEnabled = false
    fileChooser
  }
  var undoStack: mutable.Stack[Data] = mutable.Stack()
  var redoStack: mutable.Stack[Data] = mutable.Stack()
  var selectedDataSet = mutable.LinkedHashSet.empty[Data]
  var file: Option[File] = None
  val saved = dataSet

  override def top = new MainFrame {
    peer.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE)
    override def closeOperation() = quit()
    resizable = false
    menuBar = new MenuBar {
      contents += new Menu("ファイル(F)") {
        contents += new MenuItem(new Action("新規(N)") {
          override def apply() = newFile()
        })
        contents += new MenuItem(new Action("開く(O)...") {
          override def apply() = openFile()
        })
        contents += new Separator()
        contents += new MenuItem(new Action("保存(S)") {
          override def apply() = saveFile(file)
        })
        contents += new MenuItem(new Action("別名保存(A)...") {
          override def apply() = saveAs()
        })
        contents += new Separator()
        contents += new MenuItem(new Action("終了(Q)") {
          override def apply() = quit()
        })
      }
      contents += new Menu("編集(E)") {
        contents += new MenuItem(new Action("元に戻す") {
          override def apply() = undo()
        })
        contents += new MenuItem(new Action("やり直す") {
          override def apply() = redo()
        })
        contents += new Separator()
        contents += new MenuItem(new Action("削除") {
          override def apply() = delete()
        })
      }
      contents += new Menu("表示(V)") {
      }
    }
    val panel: Panel = new Panel {
      lazy val maxVValue = scrollPane.verticalScrollBar.value
      preferredSize = new Dimension(maxWidth, maxHeight)
      listenTo(mouse.clicks, mouse.moves, mouse.wheel)
      def gridLine(lines: Seq[Int], func: Int => Boolean) = lines find (func) match {
        case Some(line) => line
        case None => -1
      }
      def between(n1: Int, n2: Int)(line: Int) = line > n1 && line < n2
      def gridPoint(point: Point) = {
        val pointY = maxHeight - point.y
        val x = gridLine(vLines, between(point.x - gridSize, point.x))
        val y = gridLine(hLines, between(pointY, pointY + gridSize))
        (x, y)
      }
      reactions += {
        case MousePressed(source, point, modifiers, clicks, triggersPopup) => {
          toolBox.tools.selected match {
            case Some(button) => {
              val tool = button.asInstanceOf[Tool]
              val (x, y) = gridPoint(point)
              if (x != -1 && y != -1) {
                dataSet find (_.contains(point)) match {
                  case Some(data) => {
                    if (selectedDataSet(data)) {
                      selectedDataSet = selectedDataSet - data + data
                    } else {
                      selectedDataSet += data
                    }
                  }
                  case None => {
                    if (selectedDataSet.isEmpty) {
                      val data = new Data(tool.dataType, tool.imageData, x, y)
                      dataSet find (_.intersects(data)) match {
                        case Some(data) =>
                        case None => {
                          dataSet += data
                        }
                      }
                    }
                    selectedDataSet.clear()
                  }
                }
                panel.repaint()
              }
            }
            case None =>
          }
        }
        case MouseReleased(source, point, modifiers, clicks, triggersPopup) => {
          if (dataSet.nonEmpty) {
            redoStack.clear()
            undoStack.push(dataSet.last)
          }
        }
        case MouseDragged(_, point, modifiers) => {
          val (gridX, gridY) = gridPoint(point)
          if (gridX != -1 && gridY != -1 && dataSet.nonEmpty) {
            if (selectedDataSet.isEmpty) {
              val last = dataSet.last
              if (last.imageData.repeat) {
                val init = dataSet - last
                val newData = new Data(last, last.x, last.y, last.imageData.width + (gridX - last.x), last.imageData.height + (last.y - gridY))
                if (init forall (data => !data.intersects(newData))) {
                  dataSet.clear()
                  dataSet ++= init + newData
                }
              }
            } else {
              val last = selectedDataSet.last
              val movedData = selectedDataSet map (data => new Data(data, data.x + (gridX - last.x), data.y + (gridY - last.y), last.width, last.height))
              dataSet.clear()
              dataSet ++= dataSet -- selectedDataSet ++ movedData
              selectedDataSet = movedData
            }
            repaint()
          }
        }
        case MouseWheelMoved(_, _, _, rotation) => {
        }
      }
      override def paint(g: Graphics2D) {
        g.clearRect(0, 0, maxWidth, maxHeight)
        g.setColor(Color.GRAY)
        vLines foreach (x => g.drawLine(x, scrollPane.vValue, x, scrollPane.vValue + height))
        hLines foreach (y => g.drawLine(scrollPane.hValue, maxHeight - y, scrollPane.hValue + width, maxHeight - y))
        val repeatData = dataSet filter (d => d.imageData.repeat)
        for (data <- repeatData) {
          g.setPaint(new TexturePaint(data.imageData.image, new Rectangle(0, 0, data.imageData.width, data.imageData.height)))
          g.fillRect(data.x, data.fixY, data.width, data.height)
        }
        dataSet -- repeatData foreach (data => g.drawImage(data.imageData.image, null, data.x, data.fixY))
        g.setColor(Color.YELLOW)
        selectedDataSet foreach (data => g.drawRect(data.x, data.fixY, data.width, data.height))
        g.setColor(Color.RED)
        g.drawLine(0, maxHeight, maxWidth, maxHeight)
        g.drawLine(0, 0, 0, maxHeight)
      }
      def vLines = {
        val hStart = scrollPane.hValue / gridSize
        (hStart to vLine + hStart) map (_ * gridSize)
      }
      def hLines = {
        val vStart = (maxVValue - scrollPane.vValue) / gridSize
        (vStart to hLine + vStart) map (_ * gridSize)
      }
    }
    val scrollPane: CustomScrollPane = new CustomScrollPane(panel) {
      preferredSize = new Dimension(width, height)
      verticalScrollBar.maximum = maxHeight
      horizontalScrollBar.maximum = maxWidth
      verticalScrollBar.value = maxHeight
    }
    val toolBox = new ToolBox
    contents = new SplitPane(Orientation.Vertical, toolBox, scrollPane)
    centerOnScreen()
    actor {
      while (true) {
        panel.repaint(scrollPane.view)
        Thread.sleep(100)
      }
    }
  }

  override def quit() {
    if (isSaved) {
      super.quit()
    } else {
      Dialog.showConfirmation(null, "保存しますか？", "", Dialog.Options.YesNoCancel, Dialog.Message.Warning) match {
        case Dialog.Result.Ok => {
          saveFile(file)
          super.quit()
        }
        case Dialog.Result.No => super.quit()
        case _ =>
      }
    }
  }

  def newFile() {
    dataSet.clear()
    selectedDataSet.clear()
    undoStack.clear()
    redoStack.clear()
    saved.clear()
    file = None
  }

  def openFile() {
    def open() {
      fileChooser.showOpenDialog(null) match {
        case FileChooser.Result.Approve => {
          implicit def nodeToString(node: NodeSeq) = node.text
          implicit def nodeToInt(node: NodeSeq) = node.text.toInt
          newFile()
          val file = fileChooser.selectedFile
          val xml = XML.loadFile(file.getPath)
          val imageDataMap = (xml \ "defs" \ "images" map { images =>
            val image = (images \ "image" map { image =>
              new BufferedImage(images \ "@width", images \ "@height", BufferedImage.TYPE_4BYTE_ABGR)
            }).toArray
            ((images \ "@id").text, new ImageData(image, (images \ "@repeat").text.toBoolean))
          }).toMap
          dataSet ++= xml \ "data" map { data =>
            new Data(data \ "@type", imageDataMap((data \ "@images").text.substring(1)), data \ "@x", data \ "@y", data \ "@width", data \ "@height")
          }
        }
        case FileChooser.Result.Cancel =>
        case FileChooser.Result.Error =>
      }
    }
    if (isSaved) {
      open()
    } else {
      Dialog.showConfirmation(null, "保存しますか？", "", Dialog.Options.YesNoCancel) match {
        case Dialog.Result.Yes => {
          saveFile(file)
          open()
        }
        case Dialog.Result.No => open()
      }
    }
  }

  def saveFile(file: Option[File]) {
    file match {
      case Some(file) => {
        implicit def nodeToInt(node: Node) = node.text.toInt
        val dataList = dataSet.toList
        val dataElemList = dataList map (_.toXML)
        val imagesList = dataElemList map (e => e \ "images")
        val imagesSet = imagesList.toSet
        val imagesIdMap = imagesSet.toList zip imagesSet.toList.indices flatMap (_ match {
          case (images, index) => Map(images -> new StringBuilder("images").append(index).toString)
        }) toMap
        val imagesElemSet = imagesSet zip imagesIdMap.values map (_ match {
          case (images, id) => <images id={ id } width={ images \ "@width" } height={ images \ "@height" } repeat={ images \ "@repeat" }>{ images \ "image" }</images>
        })
        val newDataElemList = dataList zip imagesList map (_ match {
          case (data, images) => data.toXML(imagesIdMap(images))
        })
        val node = <act width={ width.toString } height={ height.toString }>
                     <defs>
                       { imagesElemSet }
                     </defs>
                     { newDataElemList }
                   </act>
        try {
          XML.write(new PrintWriter(file), Utility.trim(node), "UTF-8", true, null)
        } catch {
          case e => Dialog.showMessage(null, "ファイルを保存できませんでした。", "")
        }
      }
      case None => saveAs()
    }
  }

  def saveAs() {
    fileChooser.showSaveDialog(null) match {
      case FileChooser.Result.Approve => {
        val file = fileChooser.selectedFile
        val name = file.getName
        if (name.nonEmpty) {
          val result = if (file.exists) {
            Dialog.showConfirmation(null, "上書きしますか？", "", Dialog.Options.YesNo)
          } else {
            Dialog.Result.Yes
          }
          result match {
            case Dialog.Result.Yes => saveFile(Some(file))
            case Dialog.Result.No =>
          }
        }
      }
      case FileChooser.Result.Cancel =>
      case FileChooser.Result.Error =>
    }
  }

  def undo() {
    if (undoStack.nonEmpty) {
      selectedDataSet.clear()
      val pop = undoStack.pop
      redoStack.push(pop)
      dataSet -= pop
      println(dataSet)
    }
  }

  def redo() {
    if (redoStack.nonEmpty) {
      selectedDataSet.clear()
      val pop = redoStack.pop()
      undoStack.push(pop)
      dataSet += pop
      println(dataSet)
    }
  }

  def delete() {
    dataSet --= selectedDataSet
    selectedDataSet.clear()
  }

  def isSaved = saved == dataSet

}

class CustomScrollPane(panel: Panel) extends ScrollPane(panel) {

  def view = new Rectangle(horizontalScrollBar.value, horizontalScrollBar.value, preferredSize.width, preferredSize.height)

  def vValue = verticalScrollBar.value

  def hValue = horizontalScrollBar.value

}
