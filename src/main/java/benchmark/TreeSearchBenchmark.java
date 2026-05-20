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
public class TreeSearchBenchmark extends AbstractTreeBenchmark {
    private Tree tree;
    private BoundingBox fullIslandViewport;
    private BoundingBox townViewport;
    private BoundingBox halfIslandViewport;

    @Param({"11.0", "13.0", "16.0"})
    private double zoom;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        tree = new Tree(mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap);

        fullIslandViewport = new BoundingBox(54.97743222222222,14.377575699902936,55.316597916666666,15.506450597390476);
        townViewport = new BoundingBox(55.09362948592992,14.697506142545535,55.10374021295514,14.731158561174775);
        halfIslandViewport = new BoundingBox(55.035019812651626,14.652440745836996,55.18299668168897,15.144965124337855);
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 2, time = 2)
    @Measurement(iterations = 5, time = 2)
    public void searchFullIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consume(
                tree.search(fullIslandViewport),
                bh
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 2, time = 2)
    @Measurement(iterations = 5, time = 2)
    public void searchTown(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consume(
                tree.search(townViewport),
                bh
        );
    }

    @Benchmark
    @BenchmarkMode(Mode.AverageTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    @Warmup(iterations = 2, time = 2)
    @Measurement(iterations = 5, time = 2)
    public void searchHalfIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consume(
                tree.search(halfIslandViewport),
                bh
        );
    }
}
