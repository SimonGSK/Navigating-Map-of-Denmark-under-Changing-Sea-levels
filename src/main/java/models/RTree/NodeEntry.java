package models.RTree;

import models.geometry.BoundingBox;

public record NodeEntry(BoundingBox mbr, TreeNode child) implements TreeEntry {
    @Override
    public boolean overlaps(BoundingBox box) {
        return mbr.isOverlappingOther(box);
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }
}
