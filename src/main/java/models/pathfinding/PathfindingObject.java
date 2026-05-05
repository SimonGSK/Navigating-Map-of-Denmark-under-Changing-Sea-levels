package models.pathfinding;

import models.osm.Node;

public class PathfindingObject {
    private Node startNode;
    private Node endNode;
    private boolean isComplete;

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    public void setEndNode(Node endNode) {
        this.endNode = endNode;
    }

    public Node getStartNode() {
        return startNode;
    }

    public Node getEndNode() {
        return endNode;
    }

    public boolean isComplete() {
        return isComplete;
    }
}
