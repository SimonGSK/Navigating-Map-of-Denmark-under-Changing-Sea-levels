    package models.rendering;

    import Interfaces.Drawable;
    import models.geometry.Coordinate;
    import models.heightcurve.HeightCurve;
    import models.parser.HeightCurveData;

    import java.awt.*;
    import java.awt.geom.Path2D;
    import java.util.ArrayList;
    import java.util.List;

    public class HeightCurveRenderer implements Drawable { // TODO: Should extend AbstractRenderer and have HeightCurveData as the type
        private final HeightCurveData data;
        private final double cosMeanLat;
        private double seaLevel;

        public HeightCurveRenderer(HeightCurveData data, double meanLat) {
            this.data = data;
            this.cosMeanLat = Math.cos(Math.toRadians(meanLat));
        }

        //Bruges til at tegne height curves
        @Override
        public void draws(Graphics2D gc) {
            List<HeightCurve> curves = new ArrayList<>(data.curves);
           curves.remove(data.sea);

           for (HeightCurve curve: curves) {
               Path2D path = curve.getRegionPath(cosMeanLat);
               gc.setColor(Color.darkGray);
               gc.draw(path);
           }
        }

        private double boundingArea(HeightCurve hc) {
            double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
            double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
            for (Coordinate coord: hc.getCoords()) {
                if (coord.getLat() < minLat) minLat = coord.getLat();
                if (coord.getLat() > maxLat) maxLat = coord.getLat();
                if (coord.getLon() < minLon) minLon = coord.getLon();
                if (coord.getLon() > maxLon) maxLon = coord.getLon();
            }
            return (maxLat - minLat) * (maxLon - minLon);
        }

        public void setSeaLevel(double level) {
            this.seaLevel = level;
        }

        //Bruges kun til OSM-kortet så hver height curve fyldes helt og ikke tager højde for children
        //Ser bedre ud
        public void drawHeightCurveMap(Graphics2D gc) {
            List<HeightCurve> sorted = new ArrayList<>(data.curves);
            sorted.remove(data.sea);
            sorted.sort((a, b) -> Double.compare(boundingArea(b), boundingArea(a)));

            for (HeightCurve curve: sorted) {
                Path2D path = new Path2D.Double();
                boolean first = true;

                for (Coordinate coord : curve.getCoords()) {
                    double x = coord.getLon() * cosMeanLat;
                    double y = -coord.getLat();

                    if (first) {
                        path.moveTo(x, y);
                        first = false;
                    } else path.lineTo(x, y);
                }
                path.closePath();
                gc.setColor(curve.getFillColor(seaLevel));
                gc.fill(path);
            }
        }

        //Bruges til at farve oversvømmede height curves på OSM-kortet
        public void drawSubmergedCurves(Graphics2D gc) {
            if (seaLevel <= 0) return;

            Composite originalComposite = gc.getComposite(); //Saves the original composite

            //Farver området mellem havet og yderste height curve
            Path2D coastArea = data.sea.getRegionPath(cosMeanLat);
            gc.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f)); // 60% uigennemsigtig (værdier mellem 0.0 og 1.0)
            gc.setColor(Color.decode("#a9d3de"));
            gc.fill(coastArea);

            List<HeightCurve> sorted = new ArrayList<>(data.curves);
            sorted.remove(data.sea);
            sorted.sort((a, b) -> Double.compare(boundingArea(b), boundingArea(a)));

            //Farver alle oversvømmede height curves
            for (HeightCurve curve : sorted) {
                if (!curve.isSubmerged()) continue;

                Path2D path = curve.getRegionPath(cosMeanLat);
                gc.setColor(curve.getFillColor(seaLevel));
                gc.draw(path);
                gc.fill(path);
            }

            gc.setComposite(originalComposite); //Sets original composite again
        }
    }
