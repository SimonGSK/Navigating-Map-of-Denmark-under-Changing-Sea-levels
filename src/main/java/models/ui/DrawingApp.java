package models.ui;

import javafx.application.Application;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelBuffer;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.IntBuffer;

public abstract class DrawingApp extends Application {
    private static final int WIDTH = 1280, HEIGHT = 720;
    private final PixelBuffer<IntBuffer> pixelBuffer = new PixelBuffer<>(
            WIDTH, HEIGHT,
            IntBuffer.allocate(WIDTH * HEIGHT),
            PixelFormat.getIntArgbPreInstance()
    );
    private final BufferedImage bufferedImage = new BufferedImage(
            WIDTH,
            HEIGHT,
            BufferedImage.TYPE_INT_ARGB
    );
    protected final ImageView imageView = new ImageView();

    /**
     * Returns a Graphics2D instance used to draw unto the screen.
     */
    public final Graphics2D getNewGraphicsContext() {
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED);
        return g2d;
    }

    public static int getWIDTH() {
        return WIDTH;
    }

    public static int getHEIGHT() {
        return HEIGHT;
    }

    /**
     * Updates the screen with whatever was drawn using the Graphics Context.
     */
    public final void render() {
        // This takes the BufferedImage, and gets the pixels representation
        int[] pixels = ((DataBufferInt) this.bufferedImage.getRaster().getDataBuffer()).getData();
        // This copies our pixels into the PixelBuffer
        System.arraycopy(pixels, 0, this.pixelBuffer.getBuffer().array(), 0, pixels.length);
        // This updates our graphical component with the buffer
        this.imageView.setImage(new WritableImage(this.pixelBuffer));
    }
}
