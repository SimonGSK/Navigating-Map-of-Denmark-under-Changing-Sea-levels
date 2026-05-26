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
public class TreeMaxBenchmark_FixedZoom extends AbstractTreeBenchmark {
    private static final Map<Integer, Tree> treeCache = new HashMap<>();
    private static final double ZOOM = 11.0;
    private Tree tree;

    @Param({"30","60","90","120","150","180","210","240","270","300","330","360","390","420","450","480","510","540","570","600"})
    private int max;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        tree = treeCache.computeIfAbsent(max, m -> new Tree(max,mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap));
    }

    @Benchmark
    public void searchTownViewport(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        consumeSearchResults(
                tree.search(townViewport),
                bh
        );
    }

    @Benchmark
    public void getNearestNode_townViewport(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        bh.consume(
            tree.getNearestNode(townViewport.getCenter())
        );
    }
}
