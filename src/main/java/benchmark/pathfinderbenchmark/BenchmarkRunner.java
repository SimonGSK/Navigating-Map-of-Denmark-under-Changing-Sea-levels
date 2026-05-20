package benchmark.pathfinderbenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String label = "PathfinderBenchmark";

        new File("results").mkdirs();

        Options opt = new OptionsBuilder()
                .include("benchmark\\.PathfinderBenchmark.*")
                .result("results/" + label + ".csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();
    }
}
