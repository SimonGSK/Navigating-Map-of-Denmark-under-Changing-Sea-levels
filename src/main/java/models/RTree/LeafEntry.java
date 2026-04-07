package models.RTree;

import models.geometry.BoundingBox;
import models.osm.Element;

public record LeafEntry(BoundingBox mbr, Element data) implements TreeEntry {
    @Override
    public boolean overlaps(BoundingBox box) {
        return mbr.isOverlappingOther(box);
    }

    @Override
    public BoundingBox getMbr() {
        return mbr;
    }
}
