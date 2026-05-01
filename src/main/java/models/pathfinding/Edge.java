package models.pathfinding;

import models.osm.Node;

public class Edge {
    private Node targetNode;
    private double weight;

      public Edge(Node sourceNode, Node targetNode) {
        this.targetNode = targetNode;
        this.weight = calcDist(sourceNode, targetNode);
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

    /**
     * Haversine formula to calculate the distance between two nodes based on their coordinates.
     */
    private static double calcDist(Node v, Node w) {
        final double R = 6371000; // Earth radius in metres
        double lat1 = Math.toRadians(v.getCoordinate().getLat());
        double lat2 = Math.toRadians(w.getCoordinate().getLat());
        double dLat = Math.toRadians(w.getCoordinate().getLat() - v.getCoordinate().getLat());
        double dLon = Math.toRadians(w.getCoordinate().getLon() - v.getCoordinate().getLon());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
