package models.rendering;

import models.osm.Node;

import java.awt.*;

/**
 * Placeholder renderer for standalone nodes.
 */
public class NodeRenderer extends AbstractRenderer<Node> {
    /**
     * @param meanLat mean latitude used for projection scaling
     */
    public NodeRenderer(double meanLat) {
        super(meanLat);
    }

    /**
     * Draws a single node.
     * @param gc graphics context
     * @param node node to draw
     */
    @Override
    protected void drawElement(Graphics2D gc, Node node) {
        throw new RuntimeException("NodeRenderer.drawElement() not implemented");
    }
}
