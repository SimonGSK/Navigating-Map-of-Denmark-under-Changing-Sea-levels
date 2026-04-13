package Elements;

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
     * Haversine formula to calculate distance between two nodes based on their coordinates.
     */
    private static double calcDist(Node v, Node w) {
        final double R = 6371000; // Earth radius in metres
        double lat1 = Math.toRadians(v.getCoord().lat());
        double lat2 = Math.toRadians(w.getCoord().lat());
        double dLat = Math.toRadians(w.getCoord().lat() - v.getCoord().lat());
        double dLon = Math.toRadians(w.getCoord().lon() - v.getCoord().lon());

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }
}
