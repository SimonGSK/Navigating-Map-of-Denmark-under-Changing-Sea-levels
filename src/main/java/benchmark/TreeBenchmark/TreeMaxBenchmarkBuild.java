package benchmark.TreeBenchmark;

import models.RTree.Tree;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.SingleShotTime)
@OutputTimeUnit(TimeUnit.SECONDS)
@Warmup(iterations = 0)
@Measurement(iterations = 3)
/**
 * The goal of this benchmark is to find the most optimal max value for the Tree by measuring the performance of Tree.search() and Tree.getNearestNode()
 */
public class TreeMaxBenchmarkBuild extends AbstractTreeBenchmark {
    @Param({"5","15","30","60","90","150","210","300"})
    private int max;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
    }

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.EVENTS)
    public static class TreeSpecs {
        public double depth;
        public double nodeCount;
        public double elementCount;
    }

    @Benchmark
    public void buildTree(TreeSpecs treeSpecs, Blackhole bh) {
        Tree tree = new Tree(max,mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap);
        treeSpecs.depth = tree.getDepth();
        treeSpecs.nodeCount = tree.getNodeCount();
        treeSpecs.elementCount = tree.getElementCount();
        bh.consume(tree);
    }
}

