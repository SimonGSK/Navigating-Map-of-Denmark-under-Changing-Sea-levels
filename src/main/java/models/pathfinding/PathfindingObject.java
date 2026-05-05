package models.pathfinding;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import models.osm.Node;

public class PathfindingObject {
    private static PathfindingObject INSTANCE;
    private ObjectProperty<Node> startNode = new SimpleObjectProperty<>(null);
    private ObjectProperty<Node> endNode = new SimpleObjectProperty<>(null);

    private PathfindingObject() {};

    public static PathfindingObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PathfindingObject();
        }
        return INSTANCE;
    }

    public void setStartNode(Node startNode) {
        this.startNode.set(startNode);
    }

    public void setEndNode(Node endNode) {
        this.endNode.set(endNode);
    }

    public Node getStartNode() {
        return startNode.get();
    }

    public Node getEndNode() {
        return endNode.get();
    }

    public boolean isReady() {
        if (startNode.get().equals(endNode.get())) {
            endNode.set(null);
        }
        return startNode.get() != null && endNode.get() != null;
    }

    public void clear() {
        startNode = null;
        endNode = null;
    }

    @Override
    public String toString() {
        return super.toString() + "\nStart node: " + startNode + "\nEnd node: " + endNode;
    }
}
