package benchmark.binaryBenchmark;

import models.parser.BinaryReader;
import models.parser.OsmParser;
import models.ui.AppData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
public class BinaryBenchmark {
    String fileSystemPath = "src/main/resources/data/bornholm/bornholm.bin";
    String resourcePath = "/data/bornholm/bornholm.bin";
    AppData appData;

    @Setup(Level.Trial)
    public void setup() throws Exception {
        try{
            BinaryReader.loadForBenchmark(fileSystemPath);
        } catch (Exception e) {
            System.out.println("Binary layout not found. Writing binary file for benchmarking via AppData...");
            appData = new AppData();
            appData.parse("bornholm/bornholm.osm", "bornholm/bornholm.hc");
        }
    }

    @Benchmark
    public void loadFromOsm(Blackhole bh) throws Exception {
        appData = new AppData();
        appData.parse("bornholm/bornholm.osm", "bornholm/bornholm.hc");
        bh.consume(appData);
    }

    @Benchmark
    public void loadFromBinary(Blackhole bh) throws Exception {
        appData = new AppData();
        appData.loadFromBinary(resourcePath);
        bh.consume(appData);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BinaryBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
