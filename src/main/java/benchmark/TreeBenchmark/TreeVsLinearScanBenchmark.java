package benchmark.TreeBenchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.RTree.TreeData;
import models.geometry.BoundingBox;
import models.osm.Element;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 5, time = 2)
@Measurement(iterations = 20, time = 2)
/**
 * Compares R-Tree spatial search with naive linear scan search over all OSM elements.
 */
public class TreeVsLinearScanBenchmark extends AbstractTreeBenchmark{
    private static final double ZOOM = 30.0; // Benchmark this high means all are drawn

    @Param({"town", "halfIsland", "fullIsland"})
    private String viewport;

    private Tree tree;
    private TreeData treeData;
    private BoundingBox current;

    @Setup(Level.Trial)
    public void setup() throws ClassNotFoundException, IOException {
        super.setup();

        tree = new Tree(mapData.mbr, mapData.nodeMap, mapData.wayMap, mapData.relationMap);
        tree.setZoomLevel(ZOOM);

        treeData = new TreeData(mapData.nodeMap, mapData.wayMap, mapData.relationMap);

        Map<String, BoundingBox> viewports = new HashMap<>();

        viewports.put("town", townViewport);
        viewports.put("halfIsland", halfIslandViewport);
        viewports.put("fullIsland", fullIslandViewport);

        current = viewports.get(viewport);
    }

    @Benchmark
    public void treeSearch(Blackhole bh) {
        consumeSearchResults(tree.search(current), bh);
    }

    @Benchmark
    public void linearScanSearch(Blackhole bh) {
        SearchResults results = new SearchResults();
        treeData.forEach(e -> {
            if (!e.isVisibleOnZoom(ZOOM)) return;
            if (e.getMbr() == null) return;
            if (e.getMbr().isOverlappingOther(current)) {
                results.add(e.getType(), e);
            }
        });
        results.sort();
        consumeSearchResults(results, bh);
    }
}
