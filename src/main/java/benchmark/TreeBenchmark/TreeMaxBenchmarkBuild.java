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
public class TreeMaxBenchmarkBuild extends AbstractTreeBenchmark {
    @Param({"5","15","30","60","90","120","150","180","210","240","270","300", "330","360", "390","420","450","480","510","540","570","600"})
    private int max;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
    }

    @Benchmark
    public void buildTree(Blackhole bh) {
        bh.consume(new Tree(max,mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap));
    }
}

