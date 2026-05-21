package benchmark.AdaptivePathBenchmark;

import models.geometry.AdaptivePath;
import models.osm.Node;
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
@Measurement(iterations = 20, time = 1)
public class AdaptivePathBenchmark extends AbstractAdaptivePathBenchmark {
    private static BufferedImage BUFFERED = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    private static Graphics2D GC = BUFFERED.createGraphics();

    WayRenderer adaptiveRenderer;
    WayRenderer normalRenderer;
    private double altZoom;
    private double meanLat;

    @Param({"9","10","11","12","13","14","15","16"})  // zoom levels where simplification actually differs
    public double ZOOM_LEVEL;

    public int PIXEL_STEP = 10;

    private List<double[]> loadedPts;
    private List<Way> adaptiveWays;
    private List<Way> normalWays;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        meanLat = (mapData.mbr.maxLat() + mapData.mbr.minLat())/2.0;

        adaptiveRenderer = new WayRenderer(meanLat);
        adaptiveRenderer.setCurrentZoomLevel(ZOOM_LEVEL);
        normalRenderer = new WayRenderer(meanLat);

        adaptiveWays = new ArrayList<>(mapData.wayMap.values());

        normalWays = new ArrayList<>();
        for (Way w : mapData.wayMap.values()) {
            normalWays.add(new Way(w.getId(), w.getTags(), w.getNodes()));
        }

        for(Way w : adaptiveWays){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, PIXEL_STEP, meanLat));
        }
        adaptiveRenderer.set(adaptiveWays);

        for (Way w : adaptiveWays) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }

        for (Way w : normalWays){
            buildWay(w, meanLat);
        }
        normalRenderer.set(normalWays);

        for (Way w : adaptiveWays){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }

        altZoom = ZOOM_LEVEL + 0.5;
        GC.clearRect(0, 0, 1280, 720);
    }

    @Benchmark
    public void renderAdaptiveWays(){
        adaptiveRenderer.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer.draws(GC);
        adaptiveRenderer.setCurrentZoomLevel(altZoom);
        adaptiveRenderer.draws(GC);
    }

    @Benchmark
    public void renderNormalWays(){
        normalRenderer.setCurrentZoomLevel(ZOOM_LEVEL);
        normalRenderer.draws(GC);
        normalRenderer.setCurrentZoomLevel(altZoom);
        normalRenderer.draws(GC);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(AdaptivePathBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }

    public static void buildWay(Way way, double meanLat) {
        List<Node> nodes = way.getNodes();
        Path2D path = new Path2D.Double();

        boolean isClosed = nodes.getFirst().getId() == nodes.getLast().getId();

        boolean isFirst = true;

        for (Node node : nodes) {
            double x = node.getLon() * Math.cos(Math.toRadians(meanLat));
            double y = -node.getLat();
            if (isFirst) {
                path.moveTo(x, y);
                isFirst = false;
            } else path.lineTo(x, y);
        }
        if (isClosed) {
            path.closePath();
        }

        way.setShape(path);
    }
}
