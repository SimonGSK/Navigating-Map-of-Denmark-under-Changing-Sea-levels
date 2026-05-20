package util;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.heightcurve.HeightCurve;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.parser.HeightCurveData;
import models.parser.HeightCurveParser;
import models.parser.OsmData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for HeightCurveParser.
 *
 * Requires JUnit 5 (junit-jupiter) on the classpath.
 * OsmData and Node are stubbed with simple inner classes — no Mockito needed.
 */
class HeightCurveParserTest {

    @TempDir
    Path tempDir;


    // Stub Node that records whether setContainingHeightCurve was called.
    private static class TrackingNode extends Node {
        HeightCurve assignedCurve = null;

        TrackingNode(long id, double lat, double lon) {
            super(id, lat, lon); // Pass dummy values to the parent constructor
        }

        @Override public void setContainingHeightCurve(HeightCurve hc) {
            this.assignedCurve = hc;
        }
    }

    private HashMap<Long, Node> nodeMap = new HashMap<>();
    private OsmData osmData;

    // Mean latitude used in all tests (55° ≈ Copenhagen)
    private static final double MEAN_LAT = 55.0;

    /** Writes content to a uniquely named temp file and returns its absolute path. */
    private String writeTempFile(String content) throws IOException {
        File file = File.createTempFile("heightcurves", ".hc", tempDir.toFile());
        Files.writeString(file.toPath(), content);
        return file.getAbsolutePath();
    }

    /** Minimal valid XML for a single height curve with two coords. */
    private static String singleCurveXml(long id, double height,
                                         double lat1, double lon1,
                                         double lat2, double lon2) {
        return "<root>\n"
                + "  <hc id=\"" + id + "\" height=\"" + height + "\">\n"
                + "    <coords lat=\"" + lat1 + "\" lon=\"" + lon1 + "\"/>\n"
                + "    <coords lat=\"" + lat2 + "\" lon=\"" + lon2 + "\"/>\n"
                + "  </hc>\n"
                + "</root>\n";
    }

    @BeforeEach
    void setUp() {
        osmData = new OsmData(new BoundingBox(0.0, 0.0, 0.0, 0.0), nodeMap, new HashMap<Long, Way>(), new HashMap<Long, Relation>());
    }

    @Test
    void parseSingleCurve_createsOneHeightCurve() throws IOException {
        String xml = singleCurveXml(42, 10.0, 55.0, 12.0, 55.1, 12.1);
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        HeightCurveData data = parser.getData();
        assertNotNull(data, "getData() must not return null");
        assertEquals(1, data.curves.size(), "Expected exactly one curve");
    }

    @Test
    void parseMultipleCurves_allArePresent() throws IOException {
        String xml = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"2\" height=\"10.0\">\n"
                + "    <coords lat=\"56.0\" lon=\"13.0\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"3\" height=\"15.0\">\n"
                + "    <coords lat=\"57.0\" lon=\"14.0\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);
        assertEquals(3, parser.getData().curves.size());
    }

    record CoordCase(long id, double height, double lat1, double lon1, double lat2, double lon2) {}

    static Stream<CoordCase> coordinateCases() {
        return Stream.of(
                // Typical Danish map coordinates
                new CoordCase(1,  5.0,  55.123,  12.456, 55.789, 12.012),
                // Negative longitudes (e.g. western hemisphere)
                new CoordCase(2, 10.0,  48.500, -73.600, 48.600, -73.500),
                // Near-zero / equatorial coordinates
                new CoordCase(3,  0.5,   0.001,   0.001,  0.002,  0.002),
                // High-latitude coordinates
                new CoordCase(4, 20.0,  78.000,  15.000, 78.100, 15.100)
        );
    }

    @ParameterizedTest(name = "id={0} lat1={2} lon1={3}")
    @MethodSource("coordinateCases")
    void coordinatesAreParsedExactly(CoordCase tc) throws IOException {
        String xml = singleCurveXml(tc.id(), tc.height(),
                tc.lat1(), tc.lon1(),
                tc.lat2(), tc.lon2());
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        List<Coordinate> coords = parser.getData().curves.get(0).getCoords();
        assertEquals(2, coords.size());
        assertEquals(tc.lat1(), coords.get(0).getLat(), 1e-9, "lat1");
        assertEquals(tc.lon1(), coords.get(0).getLon(), 1e-9, "lon1");
        assertEquals(tc.lat2(), coords.get(1).getLat(), 1e-9, "lat2");
        assertEquals(tc.lon2(), coords.get(1).getLon(), 1e-9, "lon2");
    }

    @ParameterizedTest(name = "id={0} height={1}")
    @MethodSource("coordinateCases")
    void idAndHeightAreParsedExactly(CoordCase tc) throws IOException {
        String xml = singleCurveXml(tc.id(), tc.height(),
                tc.lat1(), tc.lon1(),
                tc.lat2(), tc.lon2());
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        HeightCurve curve = parser.getData().curves.get(0);
        assertEquals(tc.id(),     curve.getId());
        assertEquals(tc.height(), curve.getHeight(), 1e-9);
    }

    @Test
    void rootCurveIdentified_whenIdIsMinusOne() throws IOException {
        String xml = "<root>\n"
                + "  <hc id=\"-1\" height=\"0.0\">\n"
                + "    <coords lat=\"54.0\" lon=\"11.0\"/>\n"
                + "    <coords lat=\"54.5\" lon=\"11.5\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <coords lat=\"55.5\" lon=\"12.5\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);
        HeightCurveData data = parser.getData();

        assertNotNull(data.root, "Root (sea) curve must be identified");
        assertEquals(-1L, data.root.getId());
    }

    @Test
    void rootCurveIsNull_whenNoMinusOneId() throws IOException {
        String xml = singleCurveXml(5, 10.0, 55.0, 12.0, 55.1, 12.1);
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        assertNull(parser.getData().root, "No root curve should be found");
    }

    record BboxCase(String label,
                    double minLat, double minLon, double maxLat, double maxLon,
                    String xml) {}

    static Stream<BboxCase> bboxCases() {
        // Case 1: two regular curves, no sea curve
        String twoCurves = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <coords lat=\"56.0\" lon=\"13.0\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"2\" height=\"10.0\">\n"
                + "    <coords lat=\"54.0\" lon=\"11.0\"/>\n"
                + "    <coords lat=\"57.0\" lon=\"14.0\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        // Case 2: sea curve with extreme coords must be excluded from bbox
        String withSea = "<root>\n"
                + "  <hc id=\"-1\" height=\"0.0\">\n"
                + "    <coords lat=\"0.0\" lon=\"0.0\"/>\n"
                + "    <coords lat=\"90.0\" lon=\"180.0\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <coords lat=\"56.0\" lon=\"13.0\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        // Case 3: single coord per curve (degenerate bbox — point-like)
        String singleCoords = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"60.0\" lon=\"25.0\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        return Stream.of(
                new BboxCase("two curves",         54.0, 11.0, 57.0, 14.0, twoCurves),
                new BboxCase("sea curve excluded", 55.0, 12.0, 56.0, 13.0, withSea),
                new BboxCase("single coord",       60.0, 25.0, 60.0, 25.0, singleCoords)
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("bboxCases")
    void boundingBox_isComputedCorrectly(BboxCase tc) throws IOException {
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(tc.xml()), MEAN_LAT, osmData);
        HeightCurveData data = parser.getData();

        assertEquals(tc.minLat(), data.minLat, 1e-9, "minLat");
        assertEquals(tc.minLon(), data.minLon, 1e-9, "minLon");
        assertEquals(tc.maxLat(), data.maxLat, 1e-9, "maxLat");
        assertEquals(tc.maxLon(), data.maxLon, 1e-9, "maxLon");
    }

    @Test
    void nodeRef_linksNodeToContainingHeightCurve() throws IOException {
        TrackingNode trackingNode = new TrackingNode(999L, 0.0, 0.0);
        nodeMap.put(999L, trackingNode);

        String xml = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <nd ref=\"999\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        assertNotNull(trackingNode.assignedCurve, "Node should have been linked to a HeightCurve");
    }

    @Test
    void nodeRef_unknownNodeIsIgnored() throws IOException {
        // nodeMap is empty — referencing a non-existent node must not throw
        String xml = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <nd ref=\"12345\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        assertDoesNotThrow(() ->
                new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData));
    }

    @Test
    void nodeRef_multipleNodesLinkedCorrectly() throws IOException {
        TrackingNode node1 = new TrackingNode(1L, 0.0, 0.0);
        TrackingNode node2 = new TrackingNode(2L, 0.0, 0.0);
        nodeMap.put(1L, node1);
        nodeMap.put(2L, node2);

        String xml = "<root>\n"
                + "  <hc id=\"10\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <nd ref=\"1\"/>\n"
                + "    <nd ref=\"2\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        assertNotNull(node1.assignedCurve, "Node 1 should have been linked");
        assertNotNull(node2.assignedCurve, "Node 2 should have been linked");
    }

    @Test
    void everyNonRootCurve_hasShapeSet() throws IOException {
        String xml = "<root>\n"
                + "  <hc id=\"1\" height=\"5.0\">\n"
                + "    <coords lat=\"55.0\" lon=\"12.0\"/>\n"
                + "    <coords lat=\"55.1\" lon=\"12.1\"/>\n"
                + "  </hc>\n"
                + "  <hc id=\"2\" height=\"10.0\">\n"
                + "    <coords lat=\"56.0\" lon=\"13.0\"/>\n"
                + "    <coords lat=\"56.1\" lon=\"13.1\"/>\n"
                + "  </hc>\n"
                + "</root>\n";

        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);

        for (HeightCurve curve : parser.getData().curves) {
            assertNotNull(curve.getShape(),
                    "Shape must be set for curve id=" + curve.getId());
        }
    }

    @Test
    void emptyFile_producesNoCurvesAndDoesNotThrow() throws IOException {
        HeightCurveParser parser = new HeightCurveParser(writeTempFile(""), MEAN_LAT, osmData);

        assertNotNull(parser.getData());
        assertTrue(parser.getData().curves.isEmpty());
    }

    @Test
    void fileWithOnlyRootTag_producesNoCurves() throws IOException {
        HeightCurveParser parser = new HeightCurveParser(
                writeTempFile("<root></root>"), MEAN_LAT, osmData);

        assertTrue(parser.getData().curves.isEmpty());
    }

    @Test
    void missingFile_doesNotThrowFromConstructor() {
        // Constructor catches IOException internally and prints it
        assertDoesNotThrow(() ->
                new HeightCurveParser("/nonexistent/path/file.hc", MEAN_LAT, osmData));
    }

    @Test
    void curveWithNoCoords_isStillAdded() throws IOException {
        String xml = "<root>\n"
                + "  <hc id=\"5\" height=\"3.0\">\n"
                + "  </hc>\n"
                + "</root>\n";

        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);
        assertEquals(1, parser.getData().curves.size());
        assertTrue(parser.getData().curves.get(0).getCoords().isEmpty());
    }

    @Test
    void extraWhitespaceInLines_doesNotBreakParsing() throws IOException {
        // The parser trims each line, so extra indentation must be harmless
        String xml = "   <root>  \n"
                + "      <hc id=\"3\" height=\"7.0\">   \n"
                + "         <coords lat=\"55.0\" lon=\"12.0\"/>   \n"
                + "      </hc>  \n"
                + "   </root>  \n";

        HeightCurveParser parser = new HeightCurveParser(writeTempFile(xml), MEAN_LAT, osmData);
        assertEquals(1, parser.getData().curves.size());
        assertEquals(3L, parser.getData().curves.get(0).getId());
    }
}