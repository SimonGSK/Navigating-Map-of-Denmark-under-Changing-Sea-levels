package models.parser;

import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.osm.Node;
import models.ui.AppData;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class HeightCurveParser extends AbstractParser<HeightCurveData> {
    private final double cosMeanLat;
    private final OsmData osmData;

    public HeightCurveParser(String absoluteFilePath, double meanLat, OsmData osmData) throws IOException {
        this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
        this.osmData = osmData;
        parse(absoluteFilePath);
    }

    public void parse(String filePath) {
        this.filePath = filePath;
        List<HeightCurve> allCurves = new ArrayList<>();

        try {
            InputStream inputStream;
            File file = new File(filePath);
            if (file.isAbsolute()) {
                inputStream = new FileInputStream(file);
            } else {
               inputStream = HeightCurveParser.class.getResourceAsStream("/data/" +  filePath);
            }
            if (inputStream == null) {
                throw new FileNotFoundException("Resource not found: /data/" + filePath);
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            HeightCurve currentCurve = null;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();

                if (line.contains("<hc")) {
                    long id = getAttributeLong(line, "id");
                    double height = getAttributeDouble(line, "height");
                    currentCurve = new HeightCurve(id, height, new ArrayList<>());

                } else if (line.contains("<coords") && currentCurve != null) {
                    double lat = getAttributeDouble(line, "lat");
                    double lon = getAttributeDouble(line, "lon");
                    currentCurve.getCoords().add(new Coordinate(lat, lon));

                } else if (line.contains("<nd") && currentCurve != null){
                    long nodeID = getAttributeLong(line, "ref");
                    if (osmData.nodeMap().containsKey(nodeID)){
                        osmData.nodeMap().get(nodeID).setContainingHeightCurve(currentCurve);
                    }

                } else if (line.contains("</hc>") && currentCurve != null){
                    allCurves.add(currentCurve);
                    currentCurve = null;
                }
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

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
        this.data = new HeightCurveData(minLat, minLon, maxLat, maxLon, sea, allCurves);

        ShapeBuilder shapeBuilder = new ShapeBuilder(cosMeanLat);

        for(HeightCurve heightCurve : allCurves){
            heightCurve.setShape(shapeBuilder.buildHeightCurve(heightCurve));
        }
    }
}
