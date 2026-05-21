package models.ui;

import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.heightcurve.HeightCurve;
import models.osm.Node;
import models.osm.Way;
import models.parser.*;
import models.rendering.HeightCurveRenderer;
import models.rendering.NodeRenderer;
import models.rendering.RelationRenderer;
import models.rendering.WayRenderer;
import models.pathfinding.GraphBuilder;
import models.geometry.Coordinate;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static enums.ElementType.way;

/**
 * Holds all loaded map data and the renderers that draw it.
 *
 * Data can be loaded in two ways: from a pre-built binary file for fast startup,
 * or by parsing OSM and height curve files from scratch. After a fresh parse the
 * result is written to a binary so subsequent launches can use the fast path.
 */
public class AppData {
    private Tree tree;
    private double meanLat;

    private HeightCurveData heightCurveData;
    private HeightCurveRenderer heightCurveRenderer;

    private NodeRenderer nodeRenderer;
    private WayRenderer wayRenderer;
    private RelationRenderer relationRenderer;

    private AppDataState state = AppDataState.ready;

    public enum AppDataState {
        ready,
        error,
        complete
    }

    public AppDataState getState() {
        return state;
    }

    public AppData() {
        super();
    }

    /**
     * Loads map data from a binary file
     *
     * The adjacency graph for pathfinding is not stored in the binary and must
     * be rebuilt from the way data every time.
     *
     * Sets state error if the binary cannot be read, which causes the caller
     * to fall back to parsing the OSM file instead.
     */
    public void loadFromBinary(String binPath) {
        try {
            long start = System.currentTimeMillis();
            MapData mapData = BinaryReader.load(binPath);
            OsmData osmData = new OsmData(mapData.mbr, (HashMap<Long, Node>) mapData.nodeMap, (HashMap<Long, Way>) mapData.wayMap, (HashMap<Long, models.osm.Relation>) mapData.relationMap);
            this.tree = mapData.tree;
            init(osmData, mapData.hcData);
            try {
                buildAdjacencyGraph(osmData);
            } catch (Exception e) {
                System.out.println("Graph build failed: " + e.getMessage());
            }
            long end = System.currentTimeMillis();
            state = AppDataState.complete;
            System.out.println("Loaded from binary in " + (end - start) + " ms");
        } catch (Exception e) {
            System.out.println("Binary not found, falling back to parsing: " + e.getMessage());
            state = AppDataState.error;
        }
    }
    // Rebuilds the pathfinding adjacency graph from highway ways.
    // Separate from parsing because the graph is not serialized into the binary.
    private void buildAdjacencyGraph(OsmData osmData) {
        GraphBuilder graphBuilder = new GraphBuilder();
        for (Way way: osmData.wayMap().values()) {
            HashMap<String, String> tags = way.getTags();
            if (tags == null || !tags.containsKey("highway")) continue;
            List<Node> nodes = way.getNodes();
            if (nodes.size() < 2) continue;
            boolean isOneWay = tags.containsKey("oneway") && tags.get("oneway").equals("yes");
            for (int i = 0; i < nodes.size() - 1; i++) {
                Node from = nodes.get(i);
                Node to = nodes.get(i + 1);
                if (isOneWay) graphBuilder.connectOneWay(from, to);
                else graphBuilder.connectTwoWay(from, to);
            }
        }
    }

    /** Parses an OSM file with no height curve data. */
    public void parse(String osmFilePath) {
        try {
            OsmData osmData = parseOsm(osmFilePath);
            init(osmData);
            state = AppDataState.complete;
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found.");
        } catch (IOException ioException) {
            System.out.println("Something went wrong while reading your file.");
        } finally {
            if (state != AppDataState.complete) {
                resetAppData();
                state = AppDataState.error;
            }
        }
    }

    /**
     * Parses an OSM file together with a height curve file.
     *
     * After a successful parse the result is written to a binary so future
     * launches can skip parsing and load from binary instead.
     */
    public void parse(String osmFilePath, String heightCurveFilePath) {
        try {
            OsmData osmData = parseOsm(osmFilePath);

            meanLat = (osmData.bounds().maxLat() + osmData.bounds().minLat()) / 2.0;
            HeightCurveData heightCurveData = parseHeightCurves(heightCurveFilePath, osmData);
            init(osmData, heightCurveData);
            state = AppDataState.complete;
            try {
                String binPath = "src/main/Resources/data/" + osmFilePath.replace(".osm", ".bin");
                BinaryWriter.write(osmData, heightCurveData, tree, binPath);
                System.out.println("Binary written: " + binPath);
            } catch (Exception ex) {
                System.out.println("Could not write binary: " + ex.getMessage());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            System.out.println("File not found.");
        } catch (IOException ioException) {
            System.out.println("Something went wrong while reading your files.");
        } finally {
            if (state != AppDataState.complete) {
                resetAppData();
                state = AppDataState.error;
            }
        }
    }

    private OsmData parseOsm(String osmFilePath) throws IOException {
        OsmParser p = new OsmParser(osmFilePath);
        return p.getData();
    }

    private HeightCurveData parseHeightCurves(String heightCurveFilePath, OsmData osmData) throws IOException {
        HeightCurveParser p = new HeightCurveParser(heightCurveFilePath, meanLat, osmData);
        return p.getData();
    }

    private void resetAppData() {
        heightCurveData = null;
        heightCurveRenderer = null;

        nodeRenderer = null;
        wayRenderer = null;
        relationRenderer = null;
    }
    private BoundingBox bounds;

    public void init(OsmData osmData) {
        this.bounds = osmData.bounds();
        long start = System.currentTimeMillis();
        if (this.tree == null) {
            tree = new Tree(
                    osmData.bounds(),
                    osmData.nodeMap(),
                    osmData.wayMap(),
                    osmData.relationMap()
            );
            long end = System.currentTimeMillis();
            System.out.println("Loaded from binary in " + (end - start) + " ms");

        }

        meanLat = (osmData.bounds().maxLat() + osmData.bounds().minLat()) / 2.0;
        relationRenderer = new RelationRenderer(meanLat);
        wayRenderer = new WayRenderer(meanLat);
        nodeRenderer = new NodeRenderer(meanLat);
    }
    public BoundingBox getBounds() {
        return bounds;
    }

    public void init(OsmData osmData, HeightCurveData heightCurveData) {
        init(osmData);
        this.heightCurveData = heightCurveData;
        addCoastlineAsContour(osmData, heightCurveData);
        heightCurveRenderer = new HeightCurveRenderer(heightCurveData, meanLat);
    }

    /**
     * Adds closed coastline ways to the height curve dataset as sea-level contours.
     *
     * The flooding simulation works by checking whether an area is below sea level,
     * but it only knows about height curves. Coastlines sit at elevation 0 and define
     * the boundary between land and sea, so they need to be represented as height
     * curves for the flooding overlay to render correctly at the coast.
     *
     * Only closed coastline ways that are not already in the dataset are added.
     */
    private void addCoastlineAsContour(OsmData osmData, HeightCurveData hcData) {
        if (hcData == null || hcData.root == null) return;

        Set<Long> existingIds = new HashSet<>();
        for (HeightCurve c : hcData.curves) existingIds.add(c.getId());
        List<HeightCurve> toAdd = new ArrayList<>();
        for (Way way : osmData.wayMap().values()) {
            if (existingIds.contains(way.getId())) continue;

            HashMap<String, String> tags = way.getTags();
            if (tags == null || !"coastline".equals(tags.get("natural"))) continue;

            List<Node> nodes = way.getNodes();
            if(nodes.size() < 3) continue;
            if (nodes.get(0).getId() != nodes.get(nodes.size() - 1).getId()) continue;

            List<Coordinate> coords = new ArrayList<>();
            for (Node n : nodes) coords.add(n.getCoordinate());
            toAdd.add(new HeightCurve(way.getId(), 0.0, coords));
        }
        if (toAdd.isEmpty()) return;

        ShapeBuilder shapeBuilder = new ShapeBuilder(Math.cos(Math.toRadians(meanLat)));
        for (HeightCurve c : toAdd) {
            c.setShape(shapeBuilder.buildHeightCurve(c));
        }

        List<HeightCurve> merged = new ArrayList<>(hcData.curves);
        merged.addAll(toAdd);
        hcData.curves = merged;

        for (HeightCurve c : toAdd) hcData.root.addChild(c);

    }

    public Tree getTree() {
        return tree;
    }

    public double getMeanLat() {
        return meanLat;
    }

    public NodeRenderer getNodeRenderer() {
        return nodeRenderer;
    }

    public WayRenderer getWayRenderer() {
        return wayRenderer;
    }

    public RelationRenderer getRelationRenderer() {
        return relationRenderer;
    }

    public HeightCurveRenderer getHeightCurveRenderer() {
        return heightCurveRenderer;
    }

    public HeightCurveData getHeightCurveData() {
        return heightCurveData;
    }
}
