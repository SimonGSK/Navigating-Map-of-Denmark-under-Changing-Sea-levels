package benchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

public class BenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String label = "optimal-max-value_tree";

        new File("results").mkdirs();

        Options opt = new OptionsBuilder()
            .include("benchmark\\.TreeMax.*")
            .result("results/" + label + ".csv")
            .resultFormat(ResultFormatType.CSV)
            .build();

        new Runner(opt).run();
    }
}
