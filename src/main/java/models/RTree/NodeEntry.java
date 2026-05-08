package models.RTree;

import models.geometry.BoundingBox;

import java.io.Serializable;

public record NodeEntry(BoundingBox mbr, TreeNode child) implements TreeEntry, Serializable {
    @Override
    public boolean overlaps(BoundingBox box) {
        return mbr.isOverlappingOther(box);
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }
}
