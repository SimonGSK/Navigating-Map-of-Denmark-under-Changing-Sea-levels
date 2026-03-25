package models.heightcurve;

import java.util.List;

/**
 * Parsed height curve data.
 *
 * Intended as the output of the (optional) .hc parser as well as the hard-coded
 * examples
 */
public class HeightCurveData {

    public final double minLat;
    public final double minLon;
    public final double maxLat;
    public final double maxLon;

    /**
     * Implicit "sea" root of the containment tree. Its {@code children} are the
     * outermost coastlines.
     */
    public final HeightCurve sea;

    /**
     * Flat list of all curves (may include {@link #sea}).
     */
    public final List<HeightCurve> curves;

    public HeightCurveData(double minLat, double minLon, double maxLat, double maxLon, HeightCurve sea, List<HeightCurve> curves) {
        this.minLat = minLat;
        this.minLon = minLon;
        this.maxLat = maxLat;
        this.maxLon = maxLon;
        this.sea = sea;
        this.curves = List.copyOf(curves);
    }
}
