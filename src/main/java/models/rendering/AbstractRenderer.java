package models.rendering;

import models.osm.Element;
import models.ui.AppSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class for renderers that draw map elements.
 */
public abstract class AbstractRenderer<T extends Element> {
    protected final AppSettings appSettings = AppSettings.getInstance();
    protected final double cosMeanLat;
    protected List<T> elements = new ArrayList<>();
    protected double currentZoomLevel = 0;

    /**
     * @param meanLat mean latitude used for projection scaling
     */
    public AbstractRenderer(double meanLat) {
        cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    /**
     * @param meanLat mean latitude used for projection scaling
     * @param elements initial elements to draw
     */
    public AbstractRenderer(double meanLat, List<T> elements) {
        if (elements != null) {
            this.elements = elements;
        }
        cosMeanLat = Math.cos(Math.toRadians(meanLat));
    }

    /**
     * Replaces the current element list.
     */
    public void set(List<T> elements) {
        if (elements == null) {
            return;
        }
        this.elements = elements;
    }

    /**
     * Sets the zoom level used for visibility and sizing.
     */
    public void setCurrentZoomLevel(double zoomLevel) {
        this.currentZoomLevel = zoomLevel;
    }

    /**
     * Draws all elements in the current list.
     */
    public void draws(Graphics2D gc) {
        for (T element : elements) {
            if (!shouldDraw(element)) continue;
            drawElement(gc, element);
        }
    }

    /**
     * Draws a single element. Override when using the default loop in draws().
     */
    protected void drawElement(Graphics2D gc, T element) {
        throw new UnsupportedOperationException(
            getClass().getSimpleName() + " must override either draws() or drawElement()");
    }

    protected boolean shouldDraw(T element) {
        return element.isVisible(currentZoomLevel);
    }
}
