package benchmark.TreeBenchmark;

import models.RTree.Tree;
import models.geometry.Coordinate;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1)
@Measurement(iterations = 10, time = 1)
/**
 * The goal of this benchmark is to find the most optimal max value for the Tree by measuring the performance of Tree.search() and Tree.getNearestNode()
 */
public class TreeNearestNeighborBenchmark extends AbstractTreeBenchmark {
    private static final Map<Integer, Tree> treeCache = new HashMap<>();
    private Tree tree;

    @Param({"18.0"})
    private double ZOOM;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        tree = new Tree(mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap);
    }

    @Benchmark
    public void getNearestNode_pos1(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        bh.consume(
            tree.getNearestNode(new Coordinate(55.09313111111111,14.711422289253914))
        );
    }
    @Benchmark
    public void getNearestNode_pos2(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        bh.consume(
            tree.getNearestNode(new Coordinate(55.0996625,14.961195043144864))
        );
    }
    @Benchmark
    public void getNearestNode_pos3(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        bh.consume(
            tree.getNearestNode(new Coordinate(55.223292361111106,15.046901380264309))
        );
    }
    @Benchmark
    public void getNearestNode_pos4(Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        bh.consume(
            tree.getNearestNode(new Coordinate(55.28860625,15.474616815031823))
        );
    }
}
