package benchmark.binaryBenchmark;

import models.parser.BinaryReader;
import models.parser.OsmParser;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
public class BinaryBenchmark {
    String binPath = "src/main/resources/data/benchmarking/benchmark_bornholm.bin";

    @Setup(Level.Trial)
    public void setup() throws Exception {
        try {
            BinaryReader.loadForBenchmark(binPath);
        } catch (Exception e) {
            System.out.println("Binary layout not found. Writing binary file for benchmarking...");
            OsmParser parser = new OsmParser("bornholm/bornholm.osm");
            models.parser.BinaryWriter.writeForBenchmark(parser.getData(), binPath);
        }
    }

    @Benchmark
    public void loadFromOsm(Blackhole bh) throws Exception {
        bh.consume(new OsmParser("bornholm/bornholm.osm"));
    }

    @Benchmark
    public void loadFromBinary(Blackhole bh) throws Exception {
        bh.consume(BinaryReader.loadForBenchmark(binPath));
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BinaryBenchmark.class.getSimpleName())
                .build();

        new Runner(opt).run();
    }
}
