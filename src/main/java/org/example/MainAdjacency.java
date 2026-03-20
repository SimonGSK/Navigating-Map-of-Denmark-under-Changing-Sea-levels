package org.example;

public class MainAdjacency extends Graph {
    public static void main(String[] args) {
        // Create a graph
        Graph graph = new Graph();

        // Add vertices
        graph.addVertex(0);
        graph.addVertex(1);
        graph.addVertex(2);

        // Add edges
        graph.addEdge(0,1);
        graph.addEdge(0,2);
        graph.addEdge(1,2);

        // Print the graph
        System.out.println("Graph:");
        graph.printGraph();

        // Remove an edge and print the graph
        graph.removeEdge(0, 1);
        System.out.println("After removing edge (0, 1):");
        graph.printGraph();

        // Remove a vertex and print the graph
        graph.removeVertex(2);
        System.out.println("After removing vertex 2:");
        graph.printGraph();
    }
}
