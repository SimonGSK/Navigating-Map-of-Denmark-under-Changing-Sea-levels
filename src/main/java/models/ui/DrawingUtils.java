package models.ui;

import models.osm.Element;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Comparator;
import java.util.List;

import static java.awt.image.ImageObserver.HEIGHT;

public class DrawingUtils {
    
    public static void sortElementByArea(List<Element> elements) {
        elements.sort(Comparator.comparingDouble(o -> -o.getArea()));
    }
    
    public static void applyTransformation(Graphics2D gc) {
        double sf = HEIGHT / 0.003;

        //TODO: Find the left bound of Bornholm and insert instead of 10.
        //TODO: FInd the max latitude of Bornholm and insert instead of 0
        AffineTransform transform = AffineTransform.getTranslateInstance(-0.56 * 10., 0);

        transform.preConcatenate(AffineTransform.getScaleInstance(sf, sf));
        gc.setTransform(transform);
    }
}
