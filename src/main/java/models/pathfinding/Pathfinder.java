package models.pathfinding;

import jdk.jshell.execution.Util;
import models.osm.Node;
import models.utils.UtilityTools;

import java.util.*;

public class Pathfinder {

    // Holds results of a single Dijkstra run
    public record Result(Map<Node, Double> distances, Map<Node, Node> previousNodes) {
    }

    public Result _shortestPath(Node startNode, boolean isDijkstra) {
        if (!isDijkstra) {
            throw new RuntimeException("Must pass startNode and endNode, if you want to use A* for pathfinding");
        }
        return _shortestPath(startNode, null, true);
    }

    public Result _shortestPath(Node startNode, Node endNode, boolean isDijkstra) {
        if (startNode == null || endNode == null) {
            throw new IllegalArgumentException("startNode and endNode can't be null");
        }

        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        Set<Node> visited = new HashSet<>();

        distances.put(startNode, 0.0);

        // Priority queue comparing by known distance
        PriorityQueue<Node> queue;
        if (isDijkstra) {
            // Dijkstra comparator
            queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        } else {
            // A* comparator
            queue = new PriorityQueue<>(Comparator.comparingDouble(node -> distances.getOrDefault(node,Double.POSITIVE_INFINITY) + UtilityTools.euclideanDistance(node.getCoordinate(), endNode.getCoordinate())));
        }
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Lazy deletion: skip if already settled
            if (visited.contains(current)) {
                continue;
            }

            if(current.equals(endNode)) {
                break;
            }

            visited.add(current);

            for (Edge edge : current.getAdjacencyList()) {
                Node neighbour = edge.getTargetNode();

                if (visited.contains(neighbour)) {
                    continue;
                }

                if (neighbour.isFlooded()) {
                    continue;
                }

                double newDist = distances.get(current) + edge.getWeight();

                if (newDist < distances.getOrDefault(neighbour, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbour, newDist);
                    previousNodes.put(neighbour, current);
                    queue.add(neighbour); // lazy: old entry stays will be skipped
                }
            }
        }
        return new Result(distances, previousNodes);
    }

    //public List<Node> getShortestPathTo(Node target, Map<Node, Node> previousNodes) {
    public List<Node> getShortestPathTo(Node from, Node to) {
        Result result = _shortestPath(from, to, false);
        List<Node> path = new ArrayList<>(result.previousNodes().size());

        Node current = to;
        while (current != null) {
            path.add(current);
            current = result.previousNodes().get(current);
        }

        Collections.reverse(path);
        return path;
    }
}