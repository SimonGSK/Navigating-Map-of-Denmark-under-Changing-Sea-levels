package models.parser;

import models.RTree.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryWriter {
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
     * Write binary data for TreeBuildBenchmark.java
     * @param osmData
     * @param outPath
     * @throws IOException
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
