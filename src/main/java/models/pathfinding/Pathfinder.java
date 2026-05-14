package models.pathfinding;

import models.osm.Node;
import models.utils.UtilityTools;

import java.util.*;

public class Pathfinder {

    // Holds results of a single Dijkstra run
    public record Result(Map<Node, Double> distances, Map<Node, Node> previousNodes, Set<Node> visitedNodes) {
    }

    public Result _shortestPath(Node start, boolean isDijkstra) {
        if (!isDijkstra) {
            throw new RuntimeException("Must pass start and endNode, if you want to use A* for pathfinding");
        }
        return _shortestPath(start, null, true);
    }

    public Result _shortestPath(Node start, Node target, boolean isDijkstra) {
        if (start == null || target == null) {
            throw new IllegalArgumentException("start and target can't be null");
        }

        record QueueEntry(Node node, double fScore) {};

        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<QueueEntry> queue = new PriorityQueue<>(
                Comparator.comparingDouble(QueueEntry::fScore)
        );

        gScore.put(start, 0.0);
        double hScore = isDijkstra ?
                0.0 :
                UtilityTools.euclideanDistance(start.getCoordinate(),target.getCoordinate());
        queue.add(new QueueEntry(start, hScore));

        /*
        if (isDijkstra) {
            // Dijkstra comparator
            queue = new PriorityQueue<>(Comparator.comparingDouble(node -> gScore.getOrDefault(node,Double.POSITIVE_INFINITY)));
        } else {
            // A* comparator
            queue = new PriorityQueue<>(Comparator.comparingDouble(node -> gScore.getOrDefault(node,Double.POSITIVE_INFINITY) + UtilityTools.euclideanDistance(node.getCoordinate(), target.getCoordinate())));
        }
        queue.add(start);*/

        while (!queue.isEmpty()) {
            QueueEntry entry = queue.poll();
            Node current = entry.node();

            // Lazy deletion: skip if already settled
            if (visited.contains(current)) {
                continue;
            }

            // Early exit if target node is reached
            if (current.equals(target)) {
                break;
            }

            // Mark this node as visited
            visited.add(current);

            for (Edge edge : current.getAdjacencyList()) {
                Node neighbour = edge.getTargetNode();

                // Skip visited nodes
                if (visited.contains(neighbour)) {
                    continue;
                }

                // Skip unavailable nodes
                if (neighbour.isSubmerged()) {
                    continue;
                }

                double g = gScore.get(current) + edge.getWeight();

                // Update distance to get to this node if a shorter path is found
                if (g < gScore.getOrDefault(neighbour, Double.POSITIVE_INFINITY)) {
                    gScore.put(neighbour, g);
                    prev.put(neighbour, current);

                    double fScore = isDijkstra ?
                            g :
                            g + UtilityTools.euclideanDistance(neighbour.getCoordinate(),target.getCoordinate());
                    queue.add(new QueueEntry(neighbour, fScore)); // lazy: old entry stays will be skipped
                }
            }
        }
        return new Result(gScore, prev, visited);
    }

    public record Path(List<Node> path, Set<Node> visitedNodes) {
    }

    //public List<Node> getShortestPathTo(Node target, Map<Node, Node> previousNodes) {
    public Path getShortestPathTo(Node start, Node target) {
        Result result = _shortestPath(start, target, false);
        System.out.println("prev map size: " + result.previousNodes().size());
        System.out.println("target in prev: " + result.previousNodes().containsKey(target));

        List<Node> path = new ArrayList<>(result.previousNodes().size());

        Node current = target;
        while (current != null) {
            path.add(current);
            current = result.previousNodes().get(current);
        }

        Collections.reverse(path);
        System.out.println("path length: " + path.size());

        return new Path(path, result.visitedNodes());
    }
}