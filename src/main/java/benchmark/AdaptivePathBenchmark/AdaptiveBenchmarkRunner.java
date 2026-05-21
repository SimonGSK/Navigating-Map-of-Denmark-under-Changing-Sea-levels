package benchmark.AdaptivePathBenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

public class AdaptiveBenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String label = "adaptive-vs-fixed";
        String resultPath = "results/AdaptivePath";

        new File(resultPath).mkdirs();

        Options opt = new OptionsBuilder()
                .include("benchmark\\.AdaptivePathBenchmark\\.Adaptive.*")
                .result(resultPath + "/" + label + ".csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();
    }
}
