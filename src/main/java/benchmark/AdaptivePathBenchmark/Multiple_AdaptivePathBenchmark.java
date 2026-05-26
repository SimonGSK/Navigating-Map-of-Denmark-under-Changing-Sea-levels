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
@Measurement(iterations = 10, time = 1)
public class Multiple_AdaptivePathBenchmark extends AbstractAdaptivePathBenchmark {
    private static BufferedImage BUFFERED = new BufferedImage(1280, 720, BufferedImage.TYPE_INT_ARGB);
    private static Graphics2D GC = BUFFERED.createGraphics();

    WayRenderer adaptiveRenderer2;
    WayRenderer adaptiveRenderer4;
    WayRenderer adaptiveRenderer6;
    WayRenderer adaptiveRenderer8;
    WayRenderer adaptiveRenderer10;
    WayRenderer normalRenderer;
    private double altZoom;
    private double meanLat;

    //@Param({"9", "9.5", "10", "10.5", "11", "11.5", "12", "12.5", "13", "13.5", "14.5", "15", "15.5", "16"})  // zoom levels where simplification actually differs
    //@Param({"11","13","15"})
    public double ZOOM_LEVEL = 11;

    private List<Way> adaptiveWays2;
    private List<Way> adaptiveWays4;
    private List<Way> adaptiveWays6;
    private List<Way> adaptiveWays8;
    private List<Way> adaptiveWays10;
    private List<Way> normalWays;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        meanLat = (mapData.mbr.maxLat() + mapData.mbr.minLat())/2.0;

        adaptiveRenderer2 = new WayRenderer(meanLat);
        adaptiveRenderer4 = new WayRenderer(meanLat);
        adaptiveRenderer6 = new WayRenderer(meanLat);
        adaptiveRenderer8 = new WayRenderer(meanLat);
        adaptiveRenderer10 = new WayRenderer(meanLat);
        adaptiveRenderer2.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer4.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer6.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer8.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer10.setCurrentZoomLevel(ZOOM_LEVEL);

        normalRenderer = new WayRenderer(meanLat);

        adaptiveWays2 = new ArrayList<>();
        adaptiveWays4 = new ArrayList<>();
        adaptiveWays6 = new ArrayList<>();
        adaptiveWays8 = new ArrayList<>();
        adaptiveWays10 = new ArrayList<>();

        normalWays = new ArrayList<>();
        for (Way w : mapData.wayMap.values()) {
            normalWays.add(new Way(w.getId(), w.getTags(), w.getNodes()));
            adaptiveWays2.add(new Way(w.getId(), w.getTags(), w.getNodes()));
            adaptiveWays4.add(new Way(w.getId(), w.getTags(), w.getNodes()));
            adaptiveWays6.add(new Way(w.getId(), w.getTags(), w.getNodes()));
            adaptiveWays8.add(new Way(w.getId(), w.getTags(), w.getNodes()));
            adaptiveWays10.add(new Way(w.getId(), w.getTags(), w.getNodes()));
        }

        for(Way w : adaptiveWays2){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, 2, meanLat));
        }
        for(Way w : adaptiveWays4){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, 4, meanLat));
        }
        for(Way w : adaptiveWays6){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, 6, meanLat));
        }
        for(Way w : adaptiveWays8){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, 8, meanLat));
        }
        for(Way w : adaptiveWays10){
            w.setShape(ShapeBuilder._buildWayForBenchmark(w, 10, meanLat));
        }
        adaptiveRenderer2.set(adaptiveWays2);
        adaptiveRenderer4.set(adaptiveWays4);
        adaptiveRenderer6.set(adaptiveWays6);
        adaptiveRenderer8.set(adaptiveWays8);
        adaptiveRenderer10.set(adaptiveWays10);

        for (Way w : adaptiveWays2) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }
        for (Way w : adaptiveWays4) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }
        for (Way w : adaptiveWays6) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }
        for (Way w : adaptiveWays8) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }
        for (Way w : adaptiveWays10) {
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.updateForZoom(ZOOM_LEVEL);
            }
        }

        for (Way w : normalWays){
            buildWay(w, meanLat);
        }
        normalRenderer.set(normalWays);

        for (Way w : adaptiveWays2){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }
        for (Way w : adaptiveWays4){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }
        for (Way w : adaptiveWays6){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }
        for (Way w : adaptiveWays8){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }
        for (Way w : adaptiveWays10){
            Path2D p = w.getShape();
            if (p instanceof AdaptivePath ap) {
                ap.resetZoomCache();
            }
        }

        altZoom = ZOOM_LEVEL + 0.5;
        GC.clearRect(0, 0, 1280, 720);
    }

    @Benchmark
    public void renderAdaptiveWays2(){
        adaptiveRenderer2.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer2.draws(GC);
        adaptiveRenderer2.setCurrentZoomLevel(altZoom);
        adaptiveRenderer2.draws(GC);
    }

    @Benchmark
    public void renderAdaptiveWays4(){
        adaptiveRenderer4.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer4.draws(GC);
        adaptiveRenderer4.setCurrentZoomLevel(altZoom);
        adaptiveRenderer4.draws(GC);
    }

    @Benchmark
    public void renderAdaptiveWays6(){
        adaptiveRenderer6.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer6.draws(GC);
        adaptiveRenderer6.setCurrentZoomLevel(altZoom);
        adaptiveRenderer6.draws(GC);
    }

    @Benchmark
    public void renderAdaptiveWays8(){
        adaptiveRenderer8.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer8.draws(GC);
        adaptiveRenderer8.setCurrentZoomLevel(altZoom);
        adaptiveRenderer8.draws(GC);
    }

    @Benchmark
    public void renderAdaptiveWays10(){
        adaptiveRenderer10.setCurrentZoomLevel(ZOOM_LEVEL);
        adaptiveRenderer10.draws(GC);
        adaptiveRenderer10.setCurrentZoomLevel(altZoom);
        adaptiveRenderer10.draws(GC);
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
                .include(Multiple_AdaptivePathBenchmark.class.getSimpleName())
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
