package benchmark.TreeBenchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
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
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 15, time = 2)

/**
 * Measures how Tree.search() scales with the area of the search query.
 * The Tree is built once on the full dataset. Only viewport is changed.
 */
public class TreeFilteringPerformanceBenchmark extends AbstractTreeBenchmark{

    @Param({"30"})
    private double MAX;

    @Param({"11","30"})
    private double ZOOM;

    @Param({"small", "medium5","medium4","medium3","medium2","medium1", "large"})
    private String viewport;

    private Tree tree;
    private BoundingBox current;

    @Setup(Level.Trial)
    public void setup() throws ClassNotFoundException, IOException {
        super.setup();

        tree = new Tree(mapData.mbr, mapData.nodeMap, mapData.wayMap, mapData.relationMap);
        tree.setZoomLevel(ZOOM);

        Map<String, BoundingBox> viewports = new HashMap<>();

        BoundingBox large = new BoundingBox(54.97043430555556,14.396349468986244,55.309599999999996,15.52522436647378);
        BoundingBox medium1 = new BoundingBox(55.06464699845679,14.709925829399449,55.215387307098766,15.211648006060575);
        BoundingBox medium2 = new BoundingBox(55.1065193064129,14.849293100694204,55.17351499914266,15.072280734765817);
        BoundingBox medium3 = new BoundingBox(55.12512922106004,14.911234110158539,55.154905084495496,15.010339725301478);
        BoundingBox medium4 = new BoundingBox(55.133400294236566,14.938763447698246,55.14663401131899,14.982810387761775);
        BoundingBox medium5 = new BoundingBox(55.13707632675944,14.950998708826999,55.142957978796076,14.970575126633012);
        BoundingBox small = new BoundingBox(55.13871011899183,14.956436602662002,55.141324186563665,14.965137232798007);

        viewports.put("large", large);
        viewports.put("medium1", medium1);
        viewports.put("medium2", medium2);
        viewports.put("medium3", medium3);
        viewports.put("medium4", medium4);
        viewports.put("medium5", medium5);
        viewports.put("small", small);

        current = viewports.get(viewport);
    }

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.EVENTS)
    public static class ElementCounter {
        public double n;
    }

    @Benchmark
    public void searchSelectedViewport(ElementCounter counter, Blackhole bh) {
        tree.setZoomLevel(ZOOM);
        SearchResults sr = tree.search(current);
        counter.n = sr.size();
        consumeSearchResults(sr,bh);
    }
}
