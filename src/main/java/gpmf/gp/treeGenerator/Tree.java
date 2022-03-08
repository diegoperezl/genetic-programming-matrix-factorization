package gpmf.gp.treeGenerator;

import javafx.scene.layout.Pane;
import gpmf.gp.treeGenerator.nodeSupport.NodeTool;
import gpmf.gp.treeGenerator.nodes.Node;
import gpmf.gp.treeGenerator.nodes.Statement;

import java.util.HashMap;

public class Tree {

  private int maxDepth;
  private int numFactors;
  private int maxNodes;
  private Node root;
  private int offspring;
  private NodeTool nodeTool;
  private long seed = System.currentTimeMillis();

  public Tree(int maxDepth, int numFactors, int maxNodes, long seed) {
    this.setMaxDepth(maxDepth);
    this.setNumFactors(numFactors);
    this.setMaxNodes(maxNodes);
    this.setRoot(null);
    this.setOffspring(0);
    this.seed = seed;
  }

  public void generateTree() {
    this.nodeTool = new NodeTool(this.maxDepth, this.maxNodes, 0, this.numFactors, this.seed);
    this.root = new Statement("AssignStmt", 0, root, nodeTool);
    root.expand();
    this.setOffspring(nodeTool.getCurrentNodeNumber());
    // this.setOffspring(this.getOffspring()+this.getRoot().getOffspring()+1);
  }

  public void regenerate() {
    this.reset();
    this.generateTree();
  }

  public Tree clone() {
    Tree aux =
        new Tree(this.getMaxDepth(), this.getNumFactors(), this.getMaxNodes(), this.getSeed());
    aux.setNodeTool(this.nodeTool.clone());
    aux.setOffspring(this.getOffspring());
    aux.setRoot((Node) this.getRoot().clone(aux.getNodeTool()));
    return aux;
  }

  public void setFactorsValues(double[] factorsValues) {
    nodeTool.setFactorsValues(factorsValues);
  }

  public void setFactorsValues(HashMap<String, Double> factorsValues) {
    nodeTool.setFactorsValues(factorsValues);
  }

  public void reset() {
    nodeTool.reset();
  }

  public void restructure() {
    nodeTool.setCurrentNodeNumber(0);
    this.root.restructure(0);
    this.setOffspring(nodeTool.getCurrentNodeNumber());
  }

  public String getPrefix() {
    String prefix = root.getPrefix("");
    return prefix.replaceAll("  ", " ");
  }

  public void draw(Pane canvas, int xStart, int yStart, int xEnd, int yEnd, int[] silhouette) {
    root.draw(canvas, xStart, yStart, xEnd, yEnd, silhouette);
  }

  public void print() {
    System.out.println(this.root.toString());
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public long getSeed() {
    return this.seed;
  }

  public void setMaxDepth(int maxDepth) {
    this.maxDepth = maxDepth;
  }

  public int getMaxDepth() {
    return this.maxDepth;
  }

  public void setNumFactors(int numFactors) {
    this.numFactors = numFactors;
  }

  public int getNumFactors() {
    return this.numFactors;
  }

  public void setMaxNodes(int maxNodes) {
    this.maxNodes = maxNodes;
  }

  public int getMaxNodes() {
    return this.maxNodes;
  }

  public void setRoot(Node root) {
    this.root = root;
  }

  public Node getRoot() {
    return this.root;
  }

  public void setOffspring(int offspring) {
    this.offspring = offspring;
  }

  public int getOffspring() {
    return this.offspring;
  }

  public void setNodeTool(NodeTool nodeTool) {
    this.nodeTool = nodeTool;
  }

  public NodeTool getNodeTool() {
    return this.nodeTool;
  }

  public Node getNode(int numNode) {
    return root.getNode(numNode);
  }

  public void setNode(Node node, int numNode) {
    if (numNode == 0) root = (Node) node.clone(this.getNodeTool());
    else root.setNode(node, numNode);
  }
}