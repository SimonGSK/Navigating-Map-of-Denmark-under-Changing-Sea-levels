package benchmark;

import models.geometry.AdaptivePath;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Benchmark)
@Fork(1)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
public class AdaptivePathBenchmark {

    @Param({"0", "2", "4", "8"})
    public double PIXEL_STEP;

    @Param({"11", "15"})
    public double zoom;

    @Param({"zigzag", "straight"})
    public String shape;

    @Param({"1000", "5000", "10000"})
    public int pointCount;

    private AdaptivePath path;
    private List<double[]> loadedPts;

    @Setup(Level.Trial)
    public void loadData() throws Exception {
        Path file = Paths.get("results/adaptivePathResults/" + shape + "-" + pointCount + ".csv");
        try (var reader = Files.newBufferedReader(file)) {
            loadedPts = reader.lines().map(line -> {
                String[] parts = line.split(",");
                return new double[]{Double.parseDouble(parts[0]), Double.parseDouble(parts[1])};
            }).collect(Collectors.toList());
        }
        // Build path once per trial, not per invocation
        path = new AdaptivePath(loadedPts, false, PIXEL_STEP);
        // Warm it up with an initial call so caches are hot before measurement
        path.updateForZoom(zoom);
    }

    @Benchmark
    public int pointsRetained() {
        path.resetZoomCache();
        path.updateForZoom(zoom);
        // Don't call updateForZoom here — path is already updated in setup
        int count = 0;
        java.awt.geom.PathIterator it = path.getPathIterator(null);
        while (!it.isDone()) { count++; it.next(); }
        return count;
    }

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
}

