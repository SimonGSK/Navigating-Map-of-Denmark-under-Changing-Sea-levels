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
@BenchmarkMode(Mode.AverageTime)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)
public class TreeMaxBenchmark {
    private static volatile MapData mapData;

    private Tree tree;
    private BoundingBox fullIslandViewport;

    @Param({"5","15","30","60","120","240"})
    private int max;

    @Param({"11.0", "13.0", "16.0"})
    private double zoom;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            synchronized (TreeMaxBenchmark.class) {
                if (mapData == null) {
                    mapData = BenchmarkUtils.loadMapData();
                }
            }
        }
        System.out.println("MapData is loaded. Building tree...");
        tree = new Tree(max,mapData.mbr,mapData.nodeMap,mapData.wayMap,mapData.relationMap);

        fullIslandViewport = new BoundingBox(54.97743222222222,14.377575699902936,55.316597916666666,15.506450597390476);
    }

    private void consume(SearchResults results, Blackhole bh) {
        bh.consume(results.nodeList().size());
        bh.consume(results.wayList().size());
        bh.consume(results.relationList().size());
    }

    @Benchmark
    public void searchFullIsland(Blackhole bh) {
        tree.setZoomLevel(zoom);
        consume(
                tree.search(fullIslandViewport),
                bh
        );
    }
}
