package benchmark.TreeBenchmark;

import models.RTree.Tree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(0)
public class TreeSearchBenchmark extends AbstractTreeBenchmark {
    private static volatile Tree tree;

    @Param({"11.0", "13.0", "16.0"})
    private double zoom;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        if (tree == null) {
            tree = new Tree(mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10, time = 2)
    @Measurement(iterations = 30, time = 2)
    public void searchFullIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consumeSearchResults(
                tree.search(fullIslandViewport),
                bh
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10, time = 2)
    @Measurement(iterations = 30, time = 2)
    public void searchTown(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consumeSearchResults(
                tree.search(townViewport),
                bh
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 10, time = 2)
    @Measurement(iterations = 30, time = 2)
    public void searchHalfIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consumeSearchResults(
                tree.search(halfIslandViewport),
                bh
        );
    }
}
