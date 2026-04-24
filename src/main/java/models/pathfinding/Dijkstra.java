package models.pathfinding;

import models.osm.Node;
import java.util.*;

public class Dijkstra {

    // Holds results of a single Dijkstra run
        public record Result(Map<Node, Double> distances, Map<Node, Node> previousNodes) {
    }

    public Result shortestPath(Node startNode, Collection<Node> allNodes) {
        Map<Node, Double> distances = new HashMap<>();
        Map<Node, Node> previousNodes = new HashMap<>();
        Set<Node> visited = new HashSet<>();


        // Initialize all distances to infinity
        for (Node node : allNodes) {
            distances.put(node, Double.MAX_VALUE);
        }
        distances.put(startNode, 0.0);

        // Priority queue comparing by known distance
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingDouble(n -> distances.getOrDefault(n, Double.MAX_VALUE)));
        queue.add(startNode);

        while (!queue.isEmpty()) {
            Node current = queue.poll();

            // Lazy deletion: skip if already settled
            if (visited.contains(current)) continue;
            visited.add(current);

            for (Edge edge : current.getAdjacencyList()) {
                Node neighbour = edge.getTargetNode();

                if (visited.contains(neighbour)) continue;

                if (neighbour.isFlooded()) continue;

                double newDist = distances.get(current) + edge.getWeight();

                if (newDist < distances.getOrDefault(neighbour, Double.MAX_VALUE)) {
                    distances.put(neighbour, newDist);
                    previousNodes.put(neighbour, current);
                    queue.add(neighbour); // lazy: old entry stays, will be skipped
                }
            }
        }
        return new Result(distances, previousNodes);
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