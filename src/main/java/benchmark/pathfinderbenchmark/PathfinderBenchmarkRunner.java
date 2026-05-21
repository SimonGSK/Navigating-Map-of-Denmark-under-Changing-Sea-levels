package benchmark.pathfinderbenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

/**
 * Entry point for running the {@link PathfinderBenchmark} suite and saving
 * results to CSV files under {@code results/pathfinder/}.
 *
 * <p>Each route length (long, medium, short) is run as a separate JMH session
 * so their results are written to individual CSV files, making them easier to
 * analyse separately.
 */
public class PathfinderBenchmarkRunner {
    /**
     * Runs all three route-length benchmarks and writes results to CSV.
     *
     * @param args command-line arguments (unused)
     * @throws Exception if any JMH runner session fails
     */
    public static void main(String[] args) throws Exception {

        String resultPath = "results/pathfinder";
        new File(resultPath).mkdirs();

        runBenchmark("long-path", ".*longPath.*", resultPath + "/long-path.csv");

        runBenchmark("medium-path", ".*mediumPath.*", resultPath + "/medium-path.csv");

        runBenchmark("short-path", ".*shortPath.*", resultPath + "/short-path.csv");
    }

    /**
     * Configures and runs a single JMH session for benchmarks matching
     * {@code includePattern}, writing results to {@code outputFile} as CSV.
     *
     * @param label          a human-readable name printed to stdout before the run
     * @param includePattern a regex passed to JMH to select which benchmarks to run
     * @param outputFile     path to the CSV file where results are written
     * @throws Exception if the JMH runner fails
     */
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