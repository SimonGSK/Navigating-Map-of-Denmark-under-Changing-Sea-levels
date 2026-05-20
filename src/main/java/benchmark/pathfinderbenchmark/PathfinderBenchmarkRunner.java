package benchmark.pathfinderbenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

public class PathfinderBenchmarkRunner {

    public static void main(String[] args) throws Exception {

        String resultPath = "results/pathfinder";
        new File(resultPath).mkdirs();

        runBenchmark("long-path", ".*longPath.*", resultPath + "/long-path.csv");

        runBenchmark("medium-path", ".*mediumPath.*", resultPath + "/medium-path.csv");

        runBenchmark("short-path", ".*shortPath.*", resultPath + "/short-path.csv");
    }

    private static void runBenchmark(String label, String includePattern, String outputFile) throws Exception {
        Options opt = new OptionsBuilder()
                .include(includePattern)
                .result(outputFile)
                .resultFormat(ResultFormatType.CSV)
                .build();

        System.out.println("Running: " + label);

        new Runner(opt).run();
    }
}