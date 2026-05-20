package benchmark.TreeBenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

public class TreeBenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String label = "optimal-max-value_tree";
        String resultPath = "results/Tree";

        new File(resultPath).mkdirs();

        Options opt = new OptionsBuilder()
            .include("benchmark\\.TreeBenchmark\\.TreeMax.*")
            .result(resultPath + "/" + label + ".csv")
            .resultFormat(ResultFormatType.CSV)
            .build();

        new Runner(opt).run();
    }
}
