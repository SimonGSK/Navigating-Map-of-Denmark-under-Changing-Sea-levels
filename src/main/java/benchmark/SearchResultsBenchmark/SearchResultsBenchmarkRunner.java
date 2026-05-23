package benchmark.SearchResultsBenchmark;

import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;

public class SearchResultsBenchmarkRunner {
    public static void main(String[] args) throws Exception {
        String label = "optimal-split-value_sort_searchResults_optimum_updated";
        String resultPath = "results/SearchResults";

        new File(resultPath).mkdirs();

        Options opt = new OptionsBuilder()
            .include("benchmark\\.SearchResultsBenchmark\\.SearchResultsSortBenchmark_Optimum")
            .result(resultPath + "/" + label + ".csv")
            .resultFormat(ResultFormatType.CSV)
            .build();

        new Runner(opt).run();
    }
}
