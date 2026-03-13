package models.ui;

import java.awt.geom.AffineTransform;

public class DrawingUtils {
    public static void sortElementByArea(List<Element> elements) {
        elements.sort(Comparator.comparingDouble(o -> -o.getArea()));
    }
    public static void applyTransformation(Graphics2D gc) {
        double sf = HEIGHT/0.003;

        AffineTransform transform = AffineTransform.getTranslateInstance(-0.56 * 10.)
    }
}

