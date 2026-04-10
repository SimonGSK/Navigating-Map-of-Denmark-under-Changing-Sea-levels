package models.parser;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.heightcurve.HeightCurveData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class HCParser {
    private final String fileName;

    public HCParser(String fileName) {
        this.fileName = fileName;
    }

    public HeightCurveData parse() {
        List<HeightCurve> allCurves = new ArrayList<>();

        try {
            InputStream is = HCParser.class.getResourceAsStream("/data/" + fileName);
            if (is == null) {
                throw new IllegalArgumentException("Resource not found: " + fileName);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("<hc")) {
                    long id = getAttributeLong(line, "id");
                    double height = getAttributeDouble(line, "height");
                    ArrayList<Coordinate> coordinates = new ArrayList<>();

                    while (!line.contains("</hc>")){
                        if (line.contains("<coords")) {
                            double lat = getAttributeDouble(line, "lat");
                            double lon = getAttributeDouble(line, "lon");
                            coordinates.add(new Coordinate(lat, lon));
                        }
                    }
                    HeightCurve curve = new HeightCurve(id, height, coordinates);
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        //HeightCurveData hcData = new HeightCurveData();


        HeightCurve sea = null;
        for (HeightCurve curve : allCurves) {
            if (curve.getId() == -1) {
                sea = curve;
                break;
            }
        }
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (HeightCurve curve2 : allCurves) {
            if (curve2.getId() == -1) continue;
            for (Coordinate coord : curve2.getCoords()) {
                if (coord.getLat() < minLat) minLat = coord.getLat();
                if (coord.getLon() > maxLon) maxLon = coord.getLon();
                if (coord.getLat() > maxLat) maxLat = coord.getLat();
                if (coord.getLon() < minLon) minLon = coord.getLon();
            }
        }

        return new HeightCurveData(minLat, minLon, maxLat, maxLon, sea, allCurves);

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
