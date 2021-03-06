package gpmf.gp.treeGenerator.nodes;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import gpmf.gp.treeGenerator.nodeSupport.NodeTool;
import gpmf.gp.treeGenerator.TreeElement;

public class ConditionExpression extends Node {

  public ConditionExpression(String nodeType, int depth, Node parent, NodeTool nodeTool) {
    this.setNodeType(nodeType);
    this.setDepth(depth);
    this.setParent(parent);
    this.setOffspring(0);
    this.setNodeTool(nodeTool);

    this.setOperator(null);

    if (depth > super.getNodeTool().getDepth()) super.getNodeTool().setDepth(depth);
  }

  @Override
  public void expand() {
    switch (this.getNodeType()) {
      case "ComparisonExpression":
        comparisonExpressionExpand();
        break;
      case "MultiConditionExpression":
        multiConditionExpressionExpand();
        break;
      case "NegationExpression":
        negationExpressionExpand();
        break;
    }
  }

  public void comparisonExpressionExpand() {
    String operatorTypeSelection = this.selectComparisonOperatorExpression();
    this.setOperator(
        new Leaf(operatorTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));

    String expressionNodeTypeSelection = this.getNodeTool().selectExpression(this.getDepth() + 1);
    this.getNodeTool().addNodeNumber();
    this.setLeftNode(
        new Expression(expressionNodeTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));
    this.getLeftNode().expand();

    expressionNodeTypeSelection = this.getNodeTool().selectExpression(this.getDepth() + 1);
    this.getNodeTool().addNodeNumber();
    this.setRightNode(
        new Expression(expressionNodeTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));
    this.getRightNode().expand();
  }

  public void multiConditionExpressionExpand() {

    String operatorTypeSelection = this.selectMultiOperatorExpression();
    this.setOperator(
        new Leaf(operatorTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));

    String conditionNodeTypeSelection = this.getNodeTool().selectConditionExpression();
    this.getNodeTool().addNodeNumber();
    this.setLeftNode(
        new ConditionExpression(
            conditionNodeTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));
    this.getLeftNode().expand();

    conditionNodeTypeSelection = this.getNodeTool().selectConditionExpression();
    this.getNodeTool().addNodeNumber();
    this.setRightNode(
        new ConditionExpression(
            conditionNodeTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));
    this.getRightNode().expand();
  }

  public void negationExpressionExpand() {
    this.setOperator(new Leaf("!", this.getDepth() + 1, this, this.getNodeTool()));

    String conditionNodeTypeSelection = this.getNodeTool().selectConditionExpression();
    this.getNodeTool().addNodeNumber();
    this.setRightNode(
        new ConditionExpression(
            conditionNodeTypeSelection, this.getDepth() + 1, this, this.getNodeTool()));
    this.getRightNode().expand();
  }

  @Override
  public void draw(Pane canvas, int xStart, int yStart, int xEnd, int yEnd, int[] silhouette) {
    xEnd = silhouette[this.getDepth()];

    if (this.getLeftNode() != null)
      this.getLeftNode().draw(canvas, xEnd, yEnd, xEnd, yEnd + 100, silhouette);
    if (this.getRightNode() != null)
      this.getRightNode().draw(canvas, xEnd, yEnd, xEnd, yEnd + 100, silhouette);

    Line line = new Line(xStart, yStart, xEnd, yEnd);
    canvas.getChildren().add(line);
    Circle circle = new Circle(xStart, yStart, 30, Paint.valueOf("white"));
    circle.toBack();
    canvas.getChildren().add(circle);
    Text txt = new Text(xEnd - 5, yEnd + 15, this.getOperator().getValue());
    txt.toFront();
    canvas.getChildren().add(txt);

    if (canvas.getMinHeight() < yEnd + 100) canvas.setMinHeight(yEnd + 100);
    if (canvas.getMinWidth() < xEnd + 100) canvas.setMinWidth(xEnd + 100);
  }

  @Override
  public String getPrefix(String currentPrefix) {
    return "";
  }

  @Override
  public double eval() {
    double res = 1.0;
    switch (this.getNodeType()) {
      case "ComparisonExpression":
        if (this.getLeftNode() != null && this.getRightNode() != null)
          switch (this.getOperator().getValue()) {
            case "<":
              if (this.getLeftNode().eval() < this.getRightNode().eval()) res = 0.0;
              break;
            case ">":
              if (this.getLeftNode().eval() > this.getRightNode().eval()) res = 0.0;
              break;
            case "<=":
              if (this.getLeftNode().eval() <= this.getRightNode().eval()) res = 0.0;
              break;
            case ">=":
              if (this.getLeftNode().eval() >= this.getRightNode().eval()) res = 0.0;
              break;
          }
        break;
      case "MultiCOnditionExpression":
        if (getLeftNode() != null && getRightNode() != null)
          switch (this.getOperator().getValue()) {
            case "&&":
              if (this.getLeftNode().eval() == 0.0 && this.getRightNode().eval() == 0.0) res = 0.0;
              break;
            case "||":
              if (this.getLeftNode().eval() == 0.0 || this.getRightNode().eval() == 0.0) res = 0.0;
              break;
          }
        break;
      case "NegationExpression":
        if (getRightNode() != null) if (this.getRightNode().eval() == 1.0) res = 0.0;
        break;
    }

    return res;
  }

  @Override
  public TreeElement clone(NodeTool nodeTool) {
    ConditionExpression aux;
    aux = new ConditionExpression(this.getNodeType(), this.getDepth(), this.getParent(), nodeTool);
    aux.setNodeNumber(this.getNodeNumber());
    aux
        .setOperator(new Leaf(this.getOperator().getValue(), this.getDepth() + 1, this, nodeTool));
    if (this.getLeftNode() != null) aux.setLeftNode((Node) this.getLeftNode().clone(nodeTool));
    if (this.getRightNode() != null) aux.setRightNode((Node) this.getRightNode().clone(nodeTool));

    return aux;
  }

  @Override
  public void restructure(int depth) {
    if (depth > super.getNodeTool().getDepth()) super.getNodeTool().setDepth(depth);
    this.setDepth(depth);
    this.setNodeNumber(this.getNodeTool().getCurrentNodeNumber());

    if (this.getLeftNode() != null) {
      this.getNodeTool().addNodeNumber();
      this.getLeftNode().restructure(depth + 1);
    }
    if (this.getRightNode() != null) {
      this.getNodeTool().addNodeNumber();
      this.getRightNode().restructure(depth + 1);
    }
  }

  @Override
  public void setNode(Node node, int nodeNumber) {
    boolean found = false;
    if (this.getLeftNode() != null) {
      if (this.getLeftNode().getNodeNumber() == nodeNumber) {
        this.setLeftNode((Node) node.clone(this.getNodeTool()));
        found = true;
      } else {
        this.getLeftNode().setNode(node, nodeNumber);
      }
    }
    if (this.getRightNode() != null && !found) {
      if (this.getRightNode().getNodeNumber() == nodeNumber) {
        this.setRightNode((Node) node.clone(this.getNodeTool()));
      } else {
        this.getRightNode().setNode(node, nodeNumber);
      }
    }
  }

  @Override
  public Node getNode(int nodeNumber) {
    Node node = null;
    Node auxNode;
    boolean found = false;
    if (this.getNodeNumber() == nodeNumber) {
      node = this;
      found = true;
    }
    if (this.getLeftNode() != null && !found) {
      auxNode = this.getLeftNode().getNode(nodeNumber);
      if (auxNode != null) {
        node = auxNode;
        found = true;
      }
    }
    if (this.getRightNode() != null && !found) {
      auxNode = this.getRightNode().getNode(nodeNumber);
      if (auxNode != null) {
        node = auxNode;
      }
    }
    return node;
  }

  @Override
  public String getNodeClass() {
    return "ConditionExpression";
  }

  @Override
  public String toString() {
    String res = "";

    if (this.getNodeType().equals("ComparisonExpression")) {
      res +=
          this.getLeftNode().toString()
              + " "
              + this.getOperator()
              + " "
              + this.getRightNode().toString();
    } else if (this.getNodeType().equals("MultiConditionExpression")) {
      res +=
          this.getLeftNode().toString()
              + " "
              + this.getOperator()
              + " "
              + this.getRightNode().toString();
    } else {
      res += this.getOperator() + this.getRightNode().toString();
    }
    return res;
  }
}
