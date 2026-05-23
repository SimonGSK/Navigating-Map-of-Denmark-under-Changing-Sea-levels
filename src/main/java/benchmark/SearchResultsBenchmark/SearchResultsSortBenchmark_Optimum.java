package benchmark.SearchResultsBenchmark;

import benchmark.BenchmarkUtils;
import models.osm.Way;
import models.parser.MapData;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(2)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 20)
@Measurement(iterations = 80)
public class SearchResultsSortBenchmark_Optimum {
    public static volatile MapData mapData;

    @Param({"5000","7500","10000","12500","15000","17500","20000","22500","25000","27500","30000"})
    private int size;

    private ArrayList<Way> ways;
    private ArrayList<Way> unsortedWays;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            synchronized (SearchResultsSortBenchmark_Optimum.class) {
                if (mapData == null) {
                    mapData = BenchmarkUtils.loadMapData();
                    System.out.println("MapData is loaded....");
                }
            }
        }
        List<Way> all = new ArrayList<>(mapData.wayMap.values());
        unsortedWays = new ArrayList<>(all.subList(0,Math.min(size,all.size())));
        ways = new ArrayList<>(unsortedWays);
    }

    @Setup(Level.Iteration)
    public void restore() {
        ways.clear();
        ways.addAll(unsortedWays);
        Collections.shuffle(ways);
    }

    @Benchmark
    public void sortSequential(Blackhole bh) {
        ways.sort(Comparator.comparingDouble(Way::getArea));
        bh.consume(ways);
    }

    @Benchmark
    public void sortParallel(Blackhole bh) {
        Arrays.parallelSort(ways.toArray(new Way[0]), Comparator.comparingDouble(Way::getArea));
        bh.consume(ways);
    }
}
