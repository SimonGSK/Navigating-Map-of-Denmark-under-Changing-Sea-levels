package models.pathfinding;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import models.osm.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class PathfindingObject {
    private static PathfindingObject INSTANCE;
    private final ObjectProperty<Node> startNode = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Node> endNode = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<Node>> path = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Set<Node>> visited = new SimpleObjectProperty<>(null);
    private final Pathfinder pathfinder = new Pathfinder();

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

    public ObjectProperty<Node> getStartNodeProperty() {
        return startNode;
    }

    public ObjectProperty<Node> getEndNodeProperty() {
        return endNode;
    }

    public boolean isReady() {
        if (startNode.get() == null || endNode.get() == null) {
            return false;
        }

        if (startNode.get().equals(endNode.get())) {
            endNode.set(null);
            return false;
        }

        return true;
    }

    public void clear() {
        startNode.set(null);
        endNode.set(null);
        path.set(null);
    }

    public void updatePath() {
        Pathfinder.Path result = pathfinder.getShortestPathTo(getStartNode(),getEndNode());
        setPath(result.path());
        setVisited(result.visitedNodes());
    }

    public void setPath(List<Node> path) {
        System.out.println(Arrays.toString(path.toArray()));
        this.path.set(path);
    }

    public List<Node> getPath() {
        return path.get();
    }

    public ObjectProperty<List<Node>> getPathProperty() {
        return path;
    }

    public Set<Node> getVisited() {
        return visited.get();
    }

    public void setVisited(Set<Node> visited) {
        this.visited.set(visited);
    }

    public ObjectProperty<Set<Node>> getVisitedProperty() {
        return visited;
    }

    @Override
    public String toString() {
        return super.toString() + "\nStart node: " + startNode + "\nEnd node: " + endNode + (isReady() && path != null ? "\nPath: " + path.toString() : "");
    }
}
