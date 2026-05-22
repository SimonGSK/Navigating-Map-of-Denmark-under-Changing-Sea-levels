package benchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.parser.*;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(0)
public class TreeBuildBenchmark extends AbstractTreeBenchmark {
    @Benchmark
    @BenchmarkMode(Mode.SingleShotTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 3)
    @Measurement(iterations = 5)
    public void BuildTree(Blackhole bh) {
        bh.consume(new Tree(mapData.mbr, mapData.nodeMap, mapData.wayMap, mapData.relationMap));
    }
}