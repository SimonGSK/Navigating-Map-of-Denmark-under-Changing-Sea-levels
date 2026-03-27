package org.example;

import java.util.*;

public class DijkstraTo {

    public void shortestPath(Node startNode) {
        startNode.setMinDistance(0);
        PriorityQueue<Node> queue = new PriorityQueue<>();
        queue.add(startNode);


        while (!queue.isEmpty()) {
            Node actualNode = queue.poll();

            for (Edge edge : actualNode.getAdjacencyList()) {
                Node v = edge.getTargetNode();
                double weight = edge.getWeight();
                double currentDistance = actualNode.getMinDistance() + weight;

                if (currentDistance < v.getMinDistance()) {
                    queue.remove(v);
                    v.setMinDistance(currentDistance);
                    v.setPreviousNode(actualNode);
                    queue.add(v);
                }
            }
        }
    }

    public List<Node> getShortestPathTo(Node targetNode) {
        List<Node> path = new ArrayList<>();
        for (Node node = targetNode; node != null; node = node.getPreviousNode()) {
            path.add(node);
        }
        Collections.reverse(path);
        return path;
    }
}
