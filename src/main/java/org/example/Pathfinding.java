package org.example;

import java.util.*;

public class Pathfinding extends Path {

    public Pathfinding(Node target, double weight) {
        super(target, weight);
    }

    private static class NodeDistance {
        Node node;
        double distance;

        NodeDistance(Node node, double distance) {
            this.node = node;
            this.distance = distance;
        }
    }

    public static double shortestDistance(Node start, Node target, Map<Node, List<Path>> graph) {
        Map<Node, Double> distances = new HashMap<>();
        PriorityQueue<NodeDistance> unsettledNodes = new PriorityQueue<>(Comparator.comparingDouble(nd -> nd.distance));
        Set<Node> visited = new HashSet<>();

        distances.put(start, 0.0);
        unsettledNodes.add(new NodeDistance(start, 0.0));

        while (!unsettledNodes.isEmpty()) {
            NodeDistance current = unsettledNodes.poll();
            Node currentNode = current.node;

            if (visited.contains(currentNode)) {
                continue;
            }

            visited.add(currentNode);

            if (currentNode.equals(target)) {
                return distances.get(currentNode);
            }

            List<Path> neighbors = graph.getOrDefault(currentNode, Collections.emptyList());

            for (Path edge : neighbors) {
                Node neighbor = edge.getTarget();
                double newDistance = distances.get(currentNode) + edge.getWeight();

                if (newDistance < distances.getOrDefault(neighbor, Double.POSITIVE_INFINITY)) {
                    distances.put(neighbor, newDistance);
                    unsettledNodes.add(new NodeDistance(neighbor, newDistance));
                }
            }
        }

        return Double.POSITIVE_INFINITY;
    }
}
