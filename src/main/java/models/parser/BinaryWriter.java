package models.parser;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryWriter {
    public static void write(OsmData osmData, HeightCurveData heightCurveData, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            out.writeObject(osmData.nodeMap());
            out.writeObject(osmData.wayMap());
            out.writeObject(osmData.relationMap());
            out.writeObject(osmData.bounds());
            out.writeObject(heightCurveData);
        }
    }
}
