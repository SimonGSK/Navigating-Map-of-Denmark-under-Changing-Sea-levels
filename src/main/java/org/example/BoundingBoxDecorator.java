package org.example;

public abstract class BoundingBoxDecorator {
    private BoundingBox mbr;

    public BoundingBox getMbr() {
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }
}
