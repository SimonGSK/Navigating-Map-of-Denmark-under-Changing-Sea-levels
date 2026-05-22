package benchmark.binaryBenchmark;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

public class BinaryBenchmarkRunner {
    public static void main(String[] args) throws RunnerException {
        String label = "loading-from-binary-vs-osm";
        String resultPath = "results/binary";

        new File(resultPath).mkdirs();

        Options opt = new OptionsBuilder()
                .include("benchmark\\.binaryBenchmark\\.Binary*")
                .result(resultPath + "/" + label + ".csv")
                .resultFormat(ResultFormatType.CSV)
                .build();

        new Runner(opt).run();
    }
}
