package benchmark.TreeBenchmark;

import benchmark.BenchmarkUtils;
import models.RTree.SearchResults;
import models.geometry.BoundingBox;
import models.parser.MapData;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;

@State(Scope.Benchmark)
public abstract class AbstractTreeBenchmark {
    public static volatile MapData mapData;

    public BoundingBox fullIslandViewport;
    public BoundingBox townViewport;
    public BoundingBox halfIslandViewport;

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            synchronized (AbstractTreeBenchmark.class) {
                if (mapData == null) {
                    mapData = BenchmarkUtils.loadMapData();
                    System.out.println("MapData is loaded....");
                }
            }
        }

        fullIslandViewport = new BoundingBox(54.97743222222222,14.377575699902936,55.316597916666666,15.506450597390476);
        townViewport = new BoundingBox(55.09362948592992,14.697506142545535,55.10374021295514,14.731158561174775);
        halfIslandViewport = new BoundingBox(55.035019812651626,14.652440745836996,55.18299668168897,15.144965124337855);
    }

    public void consumeSearchResults(SearchResults results, Blackhole bh) {
        bh.consume(results.nodeList().size());
        bh.consume(results.wayList().size());
        bh.consume(results.relationList().size());
    }
}
