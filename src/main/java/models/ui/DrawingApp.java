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
    private int WIDTH = 1280, HEIGHT = 720;

    private PixelBuffer<IntBuffer> pixelBuffer;
    private BufferedImage bufferedImage;

    // Reused every frame, created once in createBuffers() and updated in place.
    // Previously a new WritableImage was allocated on every render() call, which
    // caused unnecessary garbage collection pressure.
    private WritableImage writableImage;

    protected final ImageView imageView = new ImageView();

    public DrawingApp() {
        createBuffers(WIDTH, HEIGHT);
    }

    private void createBuffers(int w, int h) {
        this.bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        this.pixelBuffer = new PixelBuffer<>(
                w, h,
                IntBuffer.allocate(w * h),
                PixelFormat.getIntArgbPreInstance()
        );
        // Create the WritableImage once, render() will update it in place
        this.writableImage = new WritableImage(this.pixelBuffer);
        this.imageView.setImage(this.writableImage);
    }

    public void resize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) return;
        WIDTH = newWidth;
        HEIGHT = newHeight;
        createBuffers(newWidth, newHeight);
    }

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

    public int getWIDTH() {
        return WIDTH;
    }

    public int getHEIGHT() {
        return HEIGHT;
    }

    /**
     * Copies the drawn pixels into the JavaFX display buffer.
     *
     * Instead of creating a new WritableImage every frame (which the previous version did),
     * we reuse the same one and call updateBuffer() to tell JavaFX the pixels have changed.
     * This avoids allocating a large object on every single render call.
     */
    public final void render() {
        // This takes the BufferedImage, and gets the pixels representation
        int[] pixels = ((DataBufferInt) this.bufferedImage.getRaster().getDataBuffer()).getData();
        // This copies our pixels into the PixelBuffer
        System.arraycopy(pixels, 0, this.pixelBuffer.getBuffer().array(), 0, pixels.length);
        // Signal JavaFX that the pixel buffer has new content, no new object allocated
        this.pixelBuffer.updateBuffer(b -> null);
    }
}
