package models.geometry;

import java.io.Serializable;

public abstract class SpatialElement implements Serializable {
    private BoundingBox mbr;
    private double area;

    public BoundingBox getMbr() {
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    public double getArea() {
        return this.area;
    }

    protected void setArea(double area) {
        this.area = area;
    }
}
