package models.ui;

import models.RTree.Tree;
import models.parser.HeightCurveData;
import models.parser.HeightCurveParser;
import models.parser.OsmData;
import models.parser.OsmParser;
import models.rendering.HeightCurveRenderer;
import models.rendering.NodeRenderer;
import models.rendering.RelationRenderer;
import models.rendering.WayRenderer;

import java.io.FileNotFoundException;
import java.io.IOException;

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
            HeightCurveData heightCurveData = parseHeightCurves(heightCurveFilePath);
            init(osmData, heightCurveData);
            state = AppDataState.complete;
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
        HeightCurveParser p = new HeightCurveParser(heightCurveFilePath);
        return p.getData();
    }

    private void resetAppData() {
        heightCurveData = null;
        heightCurveRenderer = null;

        nodeRenderer = null;
        wayRenderer = null;
        relationRenderer = null;
    }

    public void init(OsmData osmData) {
        tree = new Tree(
                osmData.bounds(),
                osmData.nodeMap(),
                osmData.wayMap(),
                osmData.relationMap()
        );

        meanLat = (tree.getMbr().maxLat() + tree.getMbr().minLat()) / 2.0;
        relationRenderer = new RelationRenderer(meanLat);
        wayRenderer = new WayRenderer(meanLat);
        nodeRenderer = new NodeRenderer(meanLat);
    }

    public void init(OsmData osmData, HeightCurveData heightCurveData) {
        init(osmData);
        heightCurveRenderer = new HeightCurveRenderer(heightCurveData, meanLat);
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
