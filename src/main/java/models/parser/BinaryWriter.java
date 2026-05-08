package models.parser;

import models.RTree.Tree;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryWriter {
    public static void write(OsmData osmData, HeightCurveData heightCurveData, Tree tree, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            // TODO: We can optimize load time if we build and save the R-Tree as a binary file instead of the osmData.
            //  Tree is the main data structure. The delay that happens between loading the binary file and seeing
            //  the application window on the screen is because the R-Tree needs to be built.
            out.writeObject(osmData.nodeMap());
            out.writeObject(osmData.wayMap());
            out.writeObject(osmData.relationMap());
            out.writeObject(osmData.bounds());
            out.writeObject(heightCurveData);
            out.writeObject(tree);
        }
    }
}
