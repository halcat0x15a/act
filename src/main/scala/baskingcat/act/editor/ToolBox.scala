package baskingcat.act.editor

import swing._

class ToolBox extends GridPanel(2, 2) {

  val tools = new ButtonGroup(new Tool("miku", "player"), new Tool("supu", "enemy"), new Tool("block", "block"))
  contents ++= tools.buttons

}
