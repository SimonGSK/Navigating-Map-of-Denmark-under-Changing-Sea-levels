package models.pathfinding;

import models.osm.Node;
import java.util.*;

public class Dijkstra {

    // Holds results of a single Dijkstra run
        public record Result(Map<Node, Double> distances, Map<Node, Node> previousNodes) {
    }

    public Result shortestPath(Node startNode, Node endNode, Collection<Node> allNodes) {
        return shortestPath(startNode, null, allNodes, false);
    }

    public Result shortestPath(Node startNode, Node endNode, Collection<Node> allNodes, boolean isDijkstra) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        Set<Node> visited = new HashSet<>();


        // Initialize all distances to infinity
        for (Node node : allNodes) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(startNode, 0.0);

        // Priority queue comparing by known distance
        PriorityQueue<Node> queue;
        if (isDijkstra) {
            queue = new PriorityQueue<>(Comparator.comparingDouble(distances::get));
        } else {
            queue = new PriorityQueue<>(Comparator.comparingDouble(node -> distances.get(node) + heuristic(node, endNode)));
        }
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Lazy deletion: skip if already settled
            if (visited.contains(current)) continue;

            if(current.equals(endNode)) break;

            visited.add(current);

            for (Edge edge : current.getAdjacencyList()) {
                Node neighbour = edge.getTargetNode();

                if (visited.contains(neighbour)) continue;

                if (neighbour.isFlooded()) continue;

                double newDist = distances.get(current) + edge.getWeight();

                if (newDist < distances.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    distances.put(neighbour, newDist);
                    previousNodes.put(neighbour, current);
                    queue.add(neighbour); // lazy: old entry stays will be skipped
                }
            }
        }
        return new Result(distances, previousNodes);
    }

    /**
     * Heuristic function: Euclidean distance on lat/lon coordinates
     * This provides an optimistic estimate of remaining distance
     */
    private double heuristic(Node current, Node endNode) {
            double lat1 = current.getLat();
            double lon1 = current.getLon();
            double lat2 = endNode.getLat();
            double lon2 = endNode.getLon();
            return Math.sqrt((lat2 - lat1) * (lat2 - lat1) + (lon2 - lon1) * (lon2 - lon1));
    }

    public List<Node> getShortestPathTo(Node target, Map<Node, Node> previousNodes) {
        List<Node> path = new ArrayList<>();
        for (Node node = target; node != null; node = previousNodes.get(node)) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
}