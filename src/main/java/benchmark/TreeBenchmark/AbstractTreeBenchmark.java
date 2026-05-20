package benchmark.TreeBenchmark;

import models.RTree.SearchResults;
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

    @Setup(Level.Trial)
    public void setup() throws IOException, ClassNotFoundException {
        if (mapData == null) {
            synchronized (TreeBuildBenchmark.class) {
                if (mapData == null) {
                    mapData = TreeBenchmarkUtils.loadMapData();
                }
            }
        }
        System.out.println("MapData is loaded....");
    }

    public void consume(SearchResults results, Blackhole bh) {
        bh.consume(results.nodeList().size());
        bh.consume(results.wayList().size());
        bh.consume(results.relationList().size());
    }
}
