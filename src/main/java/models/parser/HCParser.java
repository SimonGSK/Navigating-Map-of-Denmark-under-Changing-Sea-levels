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
                if (line.contains("coords")){
                    double minlat = getAttributeDouble(line, "minlat");
                    double minlon = getAttributeDouble(line, "minlon");
                    double maxlat = getAttributeDouble(line, "maxlat");
                    double maxlon = getAttributeDouble(line, "maxlon");
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //HeightCurveData hcData = new HeightCurveData();

        //TODO: Return the parsed HeightCurveData object.
        return null;
    }

    public String getAttribute(String s, String key) {
        String pattern = key + "=\"";
        int start = s.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int valueStart = start + pattern.length();
        int valueEnd = s.indexOf('"', valueStart);
        return s.substring(valueStart, valueEnd);
    }

    public double getAttributeDouble(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return Double.NaN;
        }
        return Double.parseDouble(val);
    }

    public long getAttributeLong(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return 0L;
        }
        return Long.parseLong(val);
    }
}
