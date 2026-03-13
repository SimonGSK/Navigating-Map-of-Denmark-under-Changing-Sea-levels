package models.geometry;

/**
 * Represents a geographic coordinate on the earth's surface.
 * <p>
 * A {@code Coordinate} is an immutable pairing of latitude and longitude values,
 * defined according to the World Geodetic System 1984 (WGS84). Coordinates are used
 * to precisely locate {@link Node}s in OpenStreetMap's spatial data model.
 * <p>
 * <b>Record Components:</b>
 * <ul>
 *   <li><b>lat</b> – the latitude value in degrees, ranging from -90° (South Pole) to +90° (North Pole).</li>
 *   <li><b>lon</b> – the longitude value in degrees, ranging from -180° (International Date Line West) to +180° (International Date Line East).</li>
 * </ul>
 * <p>
 * <b>Example:</b>
 * <br>
 * The coordinates for Copenhagen's city center are approximately {@code new Coordinate(55.6761, 12.5683)}.
 * <p>
 * <a href="https://en.wikipedia.org/wiki/World_Geodetic_System"><i>Source: Wikipedia; World Geodetic System</i></a>
 */
public record Coordinate(double lat, double lon) {
}
