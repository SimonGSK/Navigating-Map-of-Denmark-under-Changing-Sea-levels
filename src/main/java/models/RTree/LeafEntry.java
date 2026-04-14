package models.RTree;

import models.geometry.BoundingBox;

public record LeafEntry(BoundingBox mbr, EntryKey entryKey) implements TreeEntry {
    @Override
    public boolean overlaps(BoundingBox box) {
        return mbr.isOverlappingOther(box);
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }


}
