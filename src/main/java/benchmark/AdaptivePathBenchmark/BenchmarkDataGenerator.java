package benchmark.AdaptivePathBenchmark;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BenchmarkDataGenerator {
    public static void main(String[] args) throws Exception{
        Files.createDirectories(Paths.get("results"));
        generate("zigzag", 1000);
        generate("zigzag", 5000);
        generate("zigzag", 10000);
        generate("straight", 1000);
        generate("straight", 5000);
        generate("straight", 10000);
        System.out.println("Done — files written to results/");
    }

    public static void generate(String shape, int n) throws Exception {
        Path file = Paths.get("results/adaptivePathData/" + shape + "-" + n + ".csv");
        List<String> lines = new ArrayList<>();
        Random rng = new Random(42); // fixed seed for reproducibility

        double baseLon = 14.7;
        double baseLat = 55.1;
        double spanLon = 0.5;
        double spanLat = 0.4;
        double cosMeanLat = Math.cos(Math.toRadians(baseLat));

        for (int i = 0; i < n; i++) {
            double t = (double) i / (n-1);

            double x = (baseLon + t * spanLon) * cosMeanLat;
            double y;
            if (shape.equals("zigzag")) {
                y = -(baseLat + (spanLat / 2) + Math.sin(t*60) * (spanLat / 4) + rng.nextGaussian() * 0.001);
            } else {
                y = -(baseLat + t * spanLat + rng.nextGaussian() * 0.0005);
            }
            lines.add(x + "," + y);
        }
        Files.write(file, lines);
    }
}
