package org.example;

public class Path {
    private final Node target;
    private final double weight;

        public Path(Node target, double weight) {
            this.target = target;
            this.weight = weight;
        }

        public Node getTarget() {
            return target;
        }

        public double getWeight() {
            return weight;
        }

}
