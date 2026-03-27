package org.example;

import java.util.HashMap;
import java.util.Map;

public class Edge {
    Node targetNode;
    double weight;

      Edge(Node targetNode, double weight) {
        this.targetNode = targetNode;
        this.weight = weight;
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

    double calcDist(Node v, Node w) {
          double vLon = v.getCoord().lat();
          double vLat = v.getCoord().lon();
          double wLon = w.getCoord().lat();
          double wLat = w.getCoord().lon();

          double  deltaLon = wLon - vLon;
          double deltaLat = wLat - vLat;

          return Math.sqrt( (deltaLat * deltaLat) + (deltaLon * deltaLon) );
    }
}
