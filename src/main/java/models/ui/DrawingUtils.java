package models.ui;

import models.osm.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.List;

import static java.awt.image.ImageObserver.HEIGHT;

/**
 * Utility helpers for legacy drawing code.
 */
public class DrawingUtils {
    /**
     * Sorts elements by descending area.
     * @param elements elements to sort
     */
    public static void sortElementByArea(List<Element> elements) {
        elements.sort(Comparator.comparingDouble(o -> -o.getArea()));
    }
    
    /**
     * Applies a fixed transform to the graphics context.
     * @param gc graphics context
     */
    public static void applyTransformation(Graphics2D gc) {
        double sf = HEIGHT / 0.003;

        //TODO: Find the left bound of Bornholm and insert instead of 10.
        //TODO: FInd the max latitude of Bornholm and insert instead of 0
        AffineTransform transform = AffineTransform.getTranslateInstance(-0.56 * 14.68, 55.31);

        transform.preConcatenate(AffineTransform.getScaleInstance(sf, sf));
        gc.setTransform(transform);
    }
}
