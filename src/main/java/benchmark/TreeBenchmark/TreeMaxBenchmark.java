package benchmark.TreeBenchmark;

import models.RTree.Tree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@Fork(0)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 10, time = 1)
@Measurement(iterations = 30, time = 1)
/**
 * The goal of this benchmark is to find the most optimal max value for the Tree by measuring the performance of Tree.search() and Tree.getNearestNode()
 */
public class TreeMaxBenchmark extends AbstractTreeBenchmark {
    private static final Map<Integer, Tree> treeCache = new HashMap<>();
    private Tree tree;

    @Param({"5","15","30","60","120", "160", "200", "240", "300", "360", "500"})
    private int max;

    @Param({"11.0", "13.0", "16.0"})
    private double zoom;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        tree = treeCache.computeIfAbsent(max, m -> new Tree(max,mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap));
    }

    @Benchmark
    public void searchFullIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consumeSearchResults(
                tree.search(fullIslandViewport),
                bh
        );
    }

    @Benchmark
    public void getNearestNode_townViewport(Blackhole bh) {
        tree.setZoomLevel(zoom);
        bh.consume(
            tree.getNearestNode(townViewport.getCenter())
        );
    }
}
