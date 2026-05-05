package models.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class ExtSuperAffine extends SuperAffine {
    private final DoubleProperty scaleX = new SimpleDoubleProperty();
    private final DoubleProperty scaleY = new SimpleDoubleProperty();

    @Override
    public SuperAffine prependScale(double x, double y) {
        super.prependScale(x, y);
        scaleX.set(this.getScaleX());
        scaleX.set(this.getScaleY());
        return this;
    }

    public DoubleProperty scaleX() {
        return scaleX;
    }

    public DoubleProperty scaleY() {
        return scaleY;
    }
}
