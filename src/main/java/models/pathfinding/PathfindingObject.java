package models.pathfinding;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import models.osm.Node;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Singleton that holds the current pathfinding state, including the start and end nodes, the computed path,
 * and the visited node set.
 *
 * <p>Exposes JavaFX {@link ObjectProperty} fields so UI components can bind
 * directly to state changes without polling.
 */
public class PathfindingObject {
    /** The single instance of this class. */
    private static PathfindingObject INSTANCE;

    // --- Object Properties ---
    private final ObjectProperty<Node> startNode = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Node> endNode = new SimpleObjectProperty<>(null);
    private final ObjectProperty<List<Node>> path = new SimpleObjectProperty<>(null);
    private final ObjectProperty<Set<Node>> visited = new SimpleObjectProperty<>(null);

    private final Pathfinder pathfinder = new Pathfinder();
    private PathfindingObject() {};

    /**
     * Returns the singleton instance, creating it on first call.
     *
     * @return the singleton {@link PathfindingObject}
     */
    public static PathfindingObject getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PathfindingObject();
        }
        return INSTANCE;
    }

    /**
     * Returns {@code true} if both a start and end node are set and they are not the same node.
     *
     * @return {@code true} if pathfinding can proceed
     */
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

    /**
     * Clears the start node, end node, and current path.
     */
    public void clear() {
        startNode.set(null);
        endNode.set(null);
        path.set(null);
    }

    /**
     * Runs A* from the current start node to the end node and updates the path and visited set.
     * Does nothing if {@link #isReady()} returns {@code false}.
     */
    public void updatePath() {
        if(!isReady()){
            return;
        }
        Pathfinder.Path result = pathfinder.getShortestPathTo(getStartNode(),getEndNode());
        setPath(result.path());
        setVisited(result.visitedNodes());
    }

    // --- Start node ---
    public Node getStartNode() {
        return startNode.get();
    }
    public void setStartNode(Node startNode) {
        this.startNode.set(startNode);
    }

    /** @return the property backing the start node, for UI binding */
    public ObjectProperty<Node> getStartNodeProperty() {
        return startNode;
    }

    // --- End node ---
    public Node getEndNode() {
        return endNode.get();
    }
    public void setEndNode(Node endNode) {
        this.endNode.set(endNode);
    }

    /** @return the property backing the end node, for UI binding */
    public ObjectProperty<Node> getEndNodeProperty() {
        return endNode;
    }

    // --- Path ---
    public List<Node> getPath() {
        return path.get();
    }
    public void setPath(List<Node> path) {
        System.out.println(Arrays.toString(path.toArray()));
        this.path.set(path);
    }

    /** @return the property backing the path list, for UI binding */
    public ObjectProperty<List<Node>> getPathProperty() {
        return path;
    }

    // --- Visited ---
    public Set<Node> getVisited() {
        return visited.get();
    }
    public void setVisited(Set<Node> visited) {
        this.visited.set(visited);
    }

    /** @return the property backing the visited set, for UI binding */
    public ObjectProperty<Set<Node>> getVisitedProperty() {
        return visited;
    }


    @Override
    public String toString() {
        return super.toString() + "\nStart node: " + startNode + "\nEnd node: " + endNode + (isReady() && path != null ? "\nPath: " + path.toString() : "");
    }
}
