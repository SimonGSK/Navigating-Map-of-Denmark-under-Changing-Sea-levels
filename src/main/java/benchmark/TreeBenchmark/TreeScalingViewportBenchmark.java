package benchmark.TreeBenchmark;

import models.RTree.SearchResults;
import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@State(Scope.Benchmark)
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 3, time = 2)
@Measurement(iterations = 5, time = 2)


public class TreeScalingViewportBenchmark extends AbstractTreeBenchmark {
    @Param({"30"})
    private double ZOOM;

    @Param({"15","30","45","60","90"})
    private int MAX;

    @Param({"ThreeHousesRoenne", "RoenneCity","GreaterRoenne","QuarterIslandCenterRoenne","HalfIslandBottom","FullIsland", "DueoddeLighthouse", "DueoddeLighthouseGreaterArea"})
    private String viewport;

    private Tree tree;
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

    @Setup
    public void setup() throws IOException, ClassNotFoundException {
        super.setup();
        tree = new Tree(MAX,mapData.mbr, mapData.nodeMap,mapData.wayMap,mapData.relationMap);
        tree.setZoomLevel(ZOOM);

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
    public void search(TreeSpecs treeSpecs, Blackhole bh) {
        SearchResults sr = tree.searchWithBenchmark(current);
        treeSpecs.depth_nonAccTest = cachedDepth;
        treeSpecs.depth = cachedDepth;
        treeSpecs.nodeCount = cachedNodeCount;
        treeSpecs.nodesVisited = tree.getSearchNodesVisited();
        bh.consume(sr);
    }
}
