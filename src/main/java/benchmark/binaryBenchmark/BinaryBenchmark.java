package benchmark.binaryBenchmark;

import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
public class BinaryBenchmark {
    

    @Benchmark
    public void normalLoad(){

    }

    @Benchmark
    public void binaryLoad(){

    }
}
