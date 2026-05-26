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
     * Replaces the current element list.
     * @param elements new elements
     */
    public void set(List<T> elements) {
        if (elements == null) {
            return;
        }
        this.elements = elements;
    }

    /**
     * Sets the zoom level used for visibility and sizing.
     * @param zoomLevel current zoom level
     */
    public void setCurrentZoomLevel(double zoomLevel) {
        this.currentZoomLevel = zoomLevel;
    }

    /**
     * Draws all elements in the current list.
     * @param gc graphics context
     */
    public void draws(Graphics2D gc) {
        for (T element : elements) {
            if (!shouldDraw(element)) continue;
            drawElement(gc, element);
        }
    }

    /**
     * Draws a single element. Override when using the default loop in draws().
     * @param gc graphics context
     * @param element element to draw
     */
    protected void drawElement(Graphics2D gc, T element) {
        throw new UnsupportedOperationException(
            getClass().getSimpleName() + " must override either draws() or drawElement()");
    }

    /**
     * @param element element to check
     * @return true if element should be drawn
     */
    protected boolean shouldDraw(T element) {
        return element.isVisible(currentZoomLevel);
    }
}
