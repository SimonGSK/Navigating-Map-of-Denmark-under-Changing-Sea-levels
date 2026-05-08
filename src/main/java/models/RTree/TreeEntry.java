package models.RTree;

import models.geometry.BoundingBox;

public sealed interface TreeEntry permits LeafEntry, NodeEntry{
    BoundingBox getMbr();

    boolean overlaps(BoundingBox box);
}