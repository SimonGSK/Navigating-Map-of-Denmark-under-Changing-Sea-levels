package models.parser;

import models.RTree.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Writes parsed map data to binary files.
 */
public class BinaryWriter {
    /**
     * Writes full map data including height curves and tree.
     * @param osmData parsed OSM data
     * @param heightCurveData parsed height curves
     * @param tree spatial index tree
     * @param outPath output file path
     * @throws IOException when writing fails
     */
    public static void write(OsmData osmData, HeightCurveData heightCurveData, Tree tree, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            out.writeObject(osmData.nodeMap());
            out.writeObject(osmData.wayMap());
            out.writeObject(osmData.relationMap());
            out.writeObject(osmData.bounds());
            out.writeObject(heightCurveData);
            out.writeObject(tree);
        }
    }

    /**
     * Writes binary data for TreeBuildBenchmark.
     * @param osmData parsed OSM data
     * @param outPath output file path
     * @throws IOException when writing fails
     */
    public static void writeForBenchmark(OsmData osmData, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            out.writeObject(osmData.nodeMap());
            out.writeObject(osmData.wayMap());
            out.writeObject(osmData.relationMap());
            out.writeObject(osmData.bounds());
        }
    }
}
