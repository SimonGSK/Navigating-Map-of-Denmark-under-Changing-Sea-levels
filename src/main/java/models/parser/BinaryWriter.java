package models.parser;

import Interfaces.IParser;
import models.heightcurve.HeightCurveData;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

public class BinaryWriter {
    public static void write(IParser osmParser, HeightCurveData hcData, String outPath) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(outPath))) {
            out.writeObject(osmParser.getOsmNodeMap());
            out.writeObject(osmParser.getOsmWayMap());
            out.writeObject(osmParser.getOsmRelationMap());
            out.writeObject(osmParser.getBoundingBox());
            out.writeObject(hcData);
        }
    }
}
