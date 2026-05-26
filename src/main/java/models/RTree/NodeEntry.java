package models.RTree;

import models.geometry.BoundingBox;

import java.io.Serializable;

public record NodeEntry(BoundingBox mbr, TreeNode child) implements TreeEntry, Serializable {
    /**
     * @param box bounding box to test
     * @return true if this entry overlaps the box
     */
    @Override
    public boolean overlaps(BoundingBox box) {
        return mbr.isOverlappingOther(box);
    }

    /**
     * @return minimum bounding rectangle for the child node
     */
    @Override
    public BoundingBox getMbr() {
        return mbr;
    }
}
