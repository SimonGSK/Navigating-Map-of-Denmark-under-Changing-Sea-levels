package models.RTree;

import models.geometry.BoundingBox;
import models.osm.Element;

public record LeafEntry(Element element) implements TreeEntry {
    @Override
    public boolean overlaps(BoundingBox box) {
        return element.getMbr().isOverlappingOther(box);
    }

    @Override
    public BoundingBox getMbr() {
        return element.getMbr();
    }

    public Long getId() {
        return element.getId();
    }
}
