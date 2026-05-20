package models.rendering;

import models.osm.Element;
import models.ui.AppSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractRenderer<T extends Element> {
    protected final AppSettings appSettings = AppSettings.getInstance();
    protected final double cosMeanLat;
    protected List<T> elements = new ArrayList<>();
    protected double currentZoomLevel = 0;

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

    protected boolean shouldDraw(T element) {
        return element.isVisible(currentZoomLevel);
    }

    public void draws(Graphics2D gc) {
        for (T element : elements) {
            if (!shouldDraw(element)) continue;
            drawElement(gc, element);
        }
    }

    protected void drawElement(Graphics2D gc, T element) {
        throw new UnsupportedOperationException(
            getClass().getSimpleName() + " must override either draws() or drawElement()");
    }
}
