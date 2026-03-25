package models.parser;

import models.heightcurve.HeightCurveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HCParser {
    private final String fileName;

    public HCParser(String fileName) {
        this.fileName = fileName;
    }

    public HeightCurveData parse() {
        try {
            InputStream is = HCParser.class.getResourceAsStream("/" + fileName);
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + fileName);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            while ((line = br.readLine()) != null) {
                // TODO: Implement parsing
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //TODO: Return the parsed HeightCurveData object.
        return null;
    }
}
