package models.geometry;

import java.io.Serializable;

public interface SpatialElement extends Serializable {
    public BoundingBox getMbr();
    public double getArea();
}
