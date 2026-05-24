package models.pathfinding;

import models.osm.Node;
import models.utils.UtilityTools;

import java.util.*;

public class Pathfinder {

    /**
     * Provides shortest-path computation over a graph of {@link Node} objects,
     * supporting both Dijkstra's algorithm and A* search.
     *
     * <p>Dijkstra explores all reachable nodes from a source and is suitable when
     * you need distances to many targets. A* uses a haversine heuristic to guide
     * the search toward a specific target and is generally faster for point-to-point
     * queries on geographic graphs.
     *
     * <p>Submerged nodes are treated as impassable and are never relaxed.
     */
    public record Result(Map<Node, Double> distances, Map<Node, Node> previousNodes, Set<Node> visitedNodes) {
    }

    /**
     * Runs Dijkstra's algorithm from {@code source}, settling all reachable nodes.
     *
     * <p>Use this overload when you need distances to every reachable node rather
     * than a single target. Passing {@link Algorithm#A_STAR} throws because A*
     * requires a target node to compute its heuristic.
     *
     * @param source    the source node; must not be {@code null}
     * @param algorithm must be {@link Algorithm#DIJKSTRA}; passing
     *                  {@link Algorithm#A_STAR} throws {@link RuntimeException}
     * @return a {@link Result} containing distances, back-pointers, and the
     *         visited set for the full Dijkstra run
     * @throws RuntimeException if {@code algorithm} is {@link Algorithm#A_STAR}
     */
    public Result _shortestPath(Node source, Algorithm algorithm) {
        if (algorithm == Algorithm.A_STAR) {
            throw new RuntimeException("Must pass source and endNode, if you want to use A* for pathfinding");
        }
        return _shortestPath(source, null, Algorithm.DIJKSTRA);
    }

    /**
     * Runs Dijkstra or A* from {@code source} toward {@code target}.
     *
     * <p>With {@link Algorithm#DIJKSTRA} the heuristic is fixed at zero, making
     * the algorithm equivalent to standard Dijkstra. With {@link Algorithm#A_STAR}
     * a haversine distance heuristic guides the search, and the algorithm exits as
     * soon as {@code target} is settled, which is typically much faster on
     * geographic graphs.
     *
     * <p>All nodes that are {@link Node#isSubmerged()} are skipped unconditionally.
     *
     * @param source    the source node; must not be {@code null}
     * @param target    the destination node; must not be {@code null}
     * @param algorithm {@link Algorithm#DIJKSTRA} or {@link Algorithm#A_STAR}
     * @return a {@link Result} containing distances, back-pointers, and the
     *         visited set; if no path exists the back-pointer map will not
     *         contain {@code target}
     * @throws IllegalArgumentException if {@code source} or {@code target} is
     *                                  {@code null}
     */
    public Result _shortestPath(Node source, Node target, Algorithm algorithm) {
        if (source == null || target == null) {
            throw new IllegalArgumentException("source and target can't be null");
        }

        record QueueEntry(Node node, double fScore) {};

        Map<Node, Double> gScore = new HashMap<>();
        Map<Node, Node> prev = new HashMap<>();
        Set<Node> visited = new HashSet<>();
        PriorityQueue<QueueEntry> queue = new PriorityQueue<>(
                Comparator.comparingDouble(QueueEntry::fScore)
        );

        gScore.put(source, 0.0);
        double hScore = algorithm == Algorithm.DIJKSTRA ?
                0.0 :
                UtilityTools.haversineDistance(source.getCoordinate(), target.getCoordinate());
        queue.add(new QueueEntry(source, hScore));

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

                    double fScore = algorithm == Algorithm.DIJKSTRA ?
                            g :
                            g + UtilityTools.haversineDistance(neighbour.getCoordinate(), target.getCoordinate());
                    queue.add(new QueueEntry(neighbour, fScore)); // lazy: old entry stays will be skipped
                }
            }
        }
        return new Result(gScore, prev, visited);
    }

    /**
     * Holds the reconstructed node sequence of a shortest path together with
     * the set of nodes explored during the search.
     *
     * @param path         the ordered list of nodes from source to target,
     *                     inclusive; contains only the source node if no path
     *                     was found (the target will be absent)
     * @param visitedNodes the set of nodes settled during the underlying A* run,
     *                     used for visualising the search coverage
     */
    public record Path(List<Node> path, Set<Node> visitedNodes) {
    }

    /**
     * Returns the shortest path between {@code source} and {@code target} using
     * A*, together with the nodes visited during the search.
     *
     * <p>Internally calls {@link #_shortestPath(Node, Node, Algorithm)} with
     * {@link Algorithm#A_STAR} and reconstructs the path by following the
     * back-pointer chain from {@code target} back to {@code source}.
     *
     * <p>If no path exists the returned {@link Path#path()} list will begin at
     * {@code source} but will not reach {@code target}.
     *
     * @param source  the source node
     * @param target the destination node
     * @return a {@link Path} containing the node sequence and the visited set
     */
    public Path getShortestPathTo(Node source, Node target) {
        Result result = _shortestPath(source, target, Algorithm.A_STAR);
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