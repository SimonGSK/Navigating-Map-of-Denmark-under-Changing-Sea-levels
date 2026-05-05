package models.parser;

import Interfaces.Drawable;
import models.osm.Element;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRenderer<T extends Element> implements Drawable {
    protected final double cosMeanLat;
    protected List<T> elements = new ArrayList<>();
    protected double currentZoomLevel = 0;
    protected double minGeoArea = 0;

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

    public void setCurrentZoomLevel(double zoomLevel) {
        this.currentZoomLevel = zoomLevel;
    }

    public void setMinGeoArea(double minGeoArea) {
        this.minGeoArea = minGeoArea;
    }

    protected boolean shouldDraw(T element, boolean isClosed) {
        return element.isVisible(currentZoomLevel, minGeoArea, isClosed);
    }
}
