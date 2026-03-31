package org.example;

import java.util.ArrayList;
import java.util.List;

public abstract class TreeNode<T extends BoundingBoxDecorator> extends BoundingBoxDecorator {
    protected int OVERFLOW_LIMIT = 4;
    List<T> children = new ArrayList<>(OVERFLOW_LIMIT);

    public TreeNode() {
        this.mbr = new BoundingBox(0,0,0,0);
    }

    public abstract void insert(T element);

    public abstract void split();

    /**
     * Helper method to check if an element is already inside the bounds of the {@code BoundingBox} of the tree node's MBR
     * @param e
     * @return true if the element's MBR is already contained by this TreeNode's MBR, false if not
     */
    public boolean isInsideMBR(T e) {
        return e.mbr.isInsideOther(this.mbr);
    }

    public BoundingBox calcMBR() {
        if (children.isEmpty()) {
            return mbr;
        }

        if (children.size() == 1) {
            return children.getFirst().mbr;
        }

        BoundingBox firstChildMBR = children.getFirst().mbr;

        double minLat = firstChildMBR.minLat();
        double minLon = firstChildMBR.minLon();
        double maxLat = firstChildMBR.maxLat();
        double maxLon = firstChildMBR.maxLon();

        for (T c : children) {
            BoundingBox current = c.mbr;

            minLat = Math.min(minLat, current.minLat());
            minLon = Math.min(minLon, current.minLon());
            maxLat = Math.max(maxLat, current.maxLat());
            maxLon = Math.max(maxLon, current.maxLon());
        }

        return new BoundingBox(minLat,minLon,maxLat,maxLon);
    }
}
