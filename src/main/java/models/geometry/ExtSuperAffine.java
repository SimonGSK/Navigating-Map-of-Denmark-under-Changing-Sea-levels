package models.geometry;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

/**
 * SuperAffine with scale properties for UI bindings.
 */
public class ExtSuperAffine extends SuperAffine {
    private final DoubleProperty scaleX = new SimpleDoubleProperty();
    private final DoubleProperty scaleY = new SimpleDoubleProperty();

    /**
     * Prepends a scale and updates the scale properties.
     */
    @Override
    public SuperAffine prependScale(double x, double y) {
        super.prependScale(x, y);
        scaleX.set(this.getScaleX());
        scaleX.set(this.getScaleY());
        return this;
    }

    /**
     * @return X scale
     */
    public DoubleProperty scaleX() {
        return scaleX;
    }

    /**
     * @return Y scale
     */
    public DoubleProperty scaleY() {
        return scaleY;
    }
}
