package models.pathfinding;

import models.osm.Node;
import models.utils.UtilityTools;

import java.io.Serializable;

public class Edge implements Serializable {
    private Node targetNode;
    private double weight;

      public Edge(Node sourceNode, Node targetNode) {
        this.targetNode = targetNode;
        this.weight = UtilityTools.haversineDistance(sourceNode.getCoordinate(), targetNode.getCoordinate());
    }

    public Node getTargetNode() {
          return this.targetNode;
    }

    public void setTargetNode(Node targetNode) {
          this.targetNode = targetNode;
    }

    public double getWeight() {
          return this.weight;
    }

    public void setWeight(double weight) {
          this.weight = weight;
    }
}
