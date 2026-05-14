package models.ui;

import models.RTree.Tree;
import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Way;
import models.parser.*;
import models.rendering.HeightCurveRenderer;
import models.rendering.NodeRenderer;
import models.rendering.RelationRenderer;
import models.rendering.WayRenderer;
import models.pathfinding.GraphBuilder;
import models.heightcurve.HeightCurve;
import models.geometry.Coordinate;
import java.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static enums.ElementType.way;

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

    public void parse(String osmFilePath, String heightCurveFilePath) {
        try {
            OsmData osmData = parseOsm(osmFilePath);

            meanLat = (osmData.bounds().maxLat() + osmData.bounds().minLat()) / 2.0;
            HeightCurveData heightCurveData = parseHeightCurves(heightCurveFilePath);
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

    private HeightCurveData parseHeightCurves(String heightCurveFilePath) throws IOException {
        HeightCurveParser p = new HeightCurveParser(heightCurveFilePath, meanLat);
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
    private void addCoastlineAsContour(OsmData osmData, HeightCurveData hcData) {
        if (hcData == null || hcData.sea == null) return;

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

        List<HeightCurve> merged = new ArrayList<>(hcData.curves);
        merged.addAll(toAdd);
        hcData.curves = merged;

        for (HeightCurve c : toAdd) hcData.sea.addChild(c);

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
