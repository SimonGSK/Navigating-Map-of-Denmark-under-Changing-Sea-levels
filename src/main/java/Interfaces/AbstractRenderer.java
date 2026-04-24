package Interfaces;

import models.osm.Element;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRenderer<T extends Element> implements Drawable {
    protected final double cosMeanLat;
    protected List<T> elements = new ArrayList<>();

    public AbstractRenderer(double meanLat) {
        cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    public AbstractRenderer(double meanLat, List<T> elements) {
        if (elements != null) {
            this.elements = elements;
        }
        cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    public void set(List<T> elements) {
        if (elements == null) {
            return;
        }
        this.elements = elements;
    }
}
