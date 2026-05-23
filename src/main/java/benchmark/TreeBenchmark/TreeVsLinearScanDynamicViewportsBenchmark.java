package benchmark.TreeBenchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.RTree.TreeData;
import models.geometry.BoundingBox;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 8, time = 3)
/**
 * Compares R-Tree spatial search with naive linear scan search over all OSM elements.
 */
public class TreeVsLinearScanDynamicViewportsBenchmark extends AbstractTreeBenchmark{
    @Param({"30"})
    private double ZOOM; // Benchmark this high means all are drawn

    @Param({"30"})
    private int MAX;

    @Param({"ThreeHousesRoenne", "RoenneCity","GreaterRoenne","QuarterIslandCenterRoenne","HalfIslandBottom","FullIsland", "DueoddeLighthouse", "DueoddeLighthouseGreaterArea"})
    private String viewport;

    private Tree tree;
    private TreeData treeData;
    private BoundingBox current;

    BoundingBox ThreeHousesRoenne = new BoundingBox(55.097307935623604,14.702568982427799,55.09748016800383,14.703142238549377);
    BoundingBox RoenneCity = new BoundingBox(55.09260602079193, 14.690888305475479, 55.102981685414186, 14.725422539121919);
    BoundingBox GreaterRoenne = new BoundingBox(55.0718819871402, 14.613914010566955, 55.1263889638119, 14.795334356759117);
    BoundingBox QuarterIslandCenterRoenne = new BoundingBox(55.026712998946884, 14.495289992314463, 55.14569489321306, 14.891307855832794);
    BoundingBox HalfIslandBottom = new BoundingBox(54.880290645541045, 14.505931931483078, 55.11586665775981, 15.290020213405493);
    BoundingBox FullIsland = new BoundingBox(54.955318313588165, 14.392544285448482, 55.32077402835083, 15.608922561012843);
    BoundingBox DueoddeLighthouse = new BoundingBox(55.001720804983925, 15.072432992340728, 55.00213530274284, 15.073812601548758);
    BoundingBox DueoddeLighthouseGreaterArea = new BoundingBox(54.977651873226186, 14.989296757678254, 55.029563279580174, 15.162078039766028);

    private int cachedDepth;
    private int cachedNodeCount;

    @Setup(Level.Trial)
    public void setup() throws ClassNotFoundException, IOException {
        super.setup();

        tree = new Tree(mapData.mbr, mapData.nodeMap, mapData.wayMap, mapData.relationMap);
        tree.setZoomLevel(ZOOM);

        treeData = new TreeData(mapData.nodeMap, mapData.wayMap, mapData.relationMap);

        Map<String, BoundingBox> viewports = new HashMap<>();
        viewports.put("ThreeHousesRoenne",ThreeHousesRoenne);
        viewports.put("RoenneCity",RoenneCity);
        viewports.put("GreaterRoenne",GreaterRoenne);
        viewports.put("QuarterIslandCenterRoenne",QuarterIslandCenterRoenne);
        viewports.put("HalfIslandBottom",HalfIslandBottom);
        viewports.put("FullIsland",FullIsland);
        viewports.put("DueoddeLighthouse",DueoddeLighthouse);
        viewports.put("DueoddeLighthouseGreaterArea",DueoddeLighthouseGreaterArea);

        current = viewports.get(viewport);
        cachedDepth = tree.getDepth();
        cachedNodeCount = tree.getNodeCount();
    }

    @State(Scope.Thread)
    @AuxCounters(AuxCounters.Type.EVENTS)
    public static class TreeSpecs {
        public int depth;
        public int nodeCount;
        public int nodesVisited;
        public int depth_nonAccTest;
    }

    @Benchmark
    public void treeSearch(TreeSpecs treeSpecs, Blackhole bh) {
        SearchResults sr = tree.searchWithBenchmark(current);
        treeSpecs.depth_nonAccTest = cachedDepth;
        treeSpecs.depth = cachedDepth;
        treeSpecs.nodeCount = cachedNodeCount;
        treeSpecs.nodesVisited = tree.getSearchNodesVisited();
        consumeSearchResults(sr, bh);
    }

    @Benchmark
    public void linearScanSearch(TreeSpecs treeSpecs, Blackhole bh) {
        SearchResults results = new SearchResults();
        AtomicInteger count = new AtomicInteger();
        treeData.forEach(e -> {
            count.getAndIncrement();
            if (!e.isVisibleOnZoom(ZOOM)) return;
            if (e.getMbr() == null) return;
            if (e.getMbr().isOverlappingOther(current)) {
                results.add(e.getType(), e);
            }
        });
        treeSpecs.depth_nonAccTest = 0;
        treeSpecs.depth = 0;
        treeSpecs.nodeCount = treeData.size();
        treeSpecs.nodesVisited = count.get();
        results.sort();
        consumeSearchResults(results, bh);
    }
}
