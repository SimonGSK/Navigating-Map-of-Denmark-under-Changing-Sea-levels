package org.example;

public sealed interface TreeEntry permits LeafEntry, NodeEntry {
    BoundingBox getMbr();
    boolean overlaps(BoundingBox box);
}