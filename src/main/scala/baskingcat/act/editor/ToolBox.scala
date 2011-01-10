package baskingcat.act.editor

import swing._

class ToolBox extends Frame {

  val tools = new ButtonGroup(new Tool("miku", "player"), new Tool("supu", "enemy"), new Tool("block", "block"))

  contents = new FlowPanel {
    contents ++= tools.buttons
  }
  pack
  visible = true

}
