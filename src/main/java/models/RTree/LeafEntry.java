package models.RTree;

import models.geometry.BoundingBox;
import models.osm.Element;

import java.io.Serializable;

public record LeafEntry(Element element) implements TreeEntry, Serializable {
    /**
     * @param box bounding box to test
     * @return true if this entry overlaps the box
     */
    @Override
    public boolean overlaps(BoundingBox box) {
        return element.getMbr().isOverlappingOther(box);
    }

    /**
     * @return minimum bounding rectangle of the wrapped element
     */
    @Override
    public BoundingBox getMbr() {
        return element.getMbr();
    }

    /**
     * @return id of the wrapped element
     */
    public Long getId() {
        return element.getId();
    }
}
