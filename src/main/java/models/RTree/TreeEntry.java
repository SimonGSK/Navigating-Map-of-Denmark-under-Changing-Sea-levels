package models.RTree;

import models.geometry.BoundingBox;

public sealed interface TreeEntry permits LeafEntry, NodeEntry{
    /**
     * @return minimum bounding rectangle for this entry
     */
    BoundingBox getMbr();

    /**
     * @param box bounding box to test
     * @return true if this entry overlaps the box
     */
    boolean overlaps(BoundingBox box);
}