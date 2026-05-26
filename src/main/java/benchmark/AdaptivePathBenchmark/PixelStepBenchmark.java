package benchmark.AdaptivePathBenchmark;

import models.geometry.AdaptivePath;
import models.osm.Way;
import models.parser.ShapeBuilder;
import models.rendering.WayRenderer;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Benchmark)
@Fork(2)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
public class PixelStepBenchmark extends AbstractAdaptivePathBenchmark {
    private static BufferedImage BUFFERED = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    private static Graphics2D GC = BUFFERED.createGraphics();
    WayRenderer wayRenderer;
    private double altZoom;

    @Param({ "13", "13", "13", "13", "13", "13"})  // zoom levels where simplification actually differs
    public double ZOOM_LEVEL;

    @Param({"1", "4", "8", "16", "32", "64"})
    public double PIXEL_STEP;

    private List<Way> ways;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        double meanLat = (mapData.mbr.maxLat() + mapData.mbr.minLat())/2.0;
        wayRenderer = new WayRenderer(meanLat);
        wayRenderer.setCurrentZoomLevel(ZOOM_LEVEL);
        ways = new ArrayList<>(mapData.wayMap.values());

        for(Way w : ways){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, PIXEL_STEP, meanLat));
        }

        wayRenderer.set(ways);

        for (Way w : ways) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }

        long totalRaw = 0, totalKept = 0;
        for (Way w : ways) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                totalRaw += ap.getRawPointCount();
                // Count path segments after simplification
                java.awt.geom.PathIterator it = ap.getPathIterator(null);
                while (!it.isDone()) { totalKept++; it.next(); }
            }
        }
        System.out.printf("Ways: %d | Raw pts: %d | Kept pts: %d | Ratio: %.2f%%%n",
                ways.size(), totalRaw, totalKept, 100.0 * totalKept / totalRaw);

        for (Way w : ways){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }

        altZoom = ZOOM_LEVEL + 0.5;
        GC.clearRect(0, 0, 1280, 720);
    }

    @Benchmark
    public void renderBenchMark(){
        wayRenderer.setCurrentZoomLevel(ZOOM_LEVEL);
        wayRenderer.draws(GC);
        wayRenderer.setCurrentZoomLevel(altZoom);
        wayRenderer.draws(GC);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(PixelStepBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}

