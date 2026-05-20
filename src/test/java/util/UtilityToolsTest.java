package util;

import models.geometry.Coordinate;
import models.utils.UtilityTools;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UtilityToolsTest {


    //  Helpers

    private static Coordinate coord(double lat, double lon) {
        return new Coordinate(lat, lon);
    }


    //  euclideanDistance()

    @Nested
    @DisplayName("euclideanDistance()")
    class EuclideanDistanceTests {

        @Test
        @DisplayName("Same point returns 0")
        void euclidean_samePoint_returnsZero() {
            assertEquals(0.0, UtilityTools.euclideanDistance(coord(55.0, 12.0), coord(55.0, 12.0)), 1e-12);
        }

        @Test
        @DisplayName("3-4-5 right triangle returns 5")
        void euclidean_345Triangle_returnsFive() {
            assertEquals(5.0, UtilityTools.euclideanDistance(coord(0.0, 0.0), coord(3.0, 4.0)), 1e-12);
        }

        @Test
        @DisplayName("Is symmetric: dist(a,b) == dist(b,a)")
        void euclidean_symmetric() {
            Coordinate a = coord(55.0, 12.0);
            Coordinate b = coord(56.0, 13.0);
            assertEquals(
                    UtilityTools.euclideanDistance(a, b),
                    UtilityTools.euclideanDistance(b, a),
                    1e-12
            );
        }

        @Test
        @DisplayName("Is never negative")
        void euclidean_neverNegative() {
            assertTrue(UtilityTools.euclideanDistance(coord(1.0, 2.0), coord(3.0, 4.0)) >= 0.0);
        }

        @Test
        @DisplayName("Horizontal offset of 1 degree returns exactly 1.0")
        void euclidean_horizontalOffsetOneUnit_returnsOne() {
            assertEquals(1.0, UtilityTools.euclideanDistance(coord(0.0, 0.0), coord(0.0, 1.0)), 1e-12);
        }

        @Test
        @DisplayName("Vertical offset of 1 degree returns exactly 1.0")
        void euclidean_verticalOffsetOneUnit_returnsOne() {
            assertEquals(1.0, UtilityTools.euclideanDistance(coord(0.0, 0.0), coord(1.0, 0.0)), 1e-12);
        }
    }


    //  haversineDistance()

    @Nested
    @DisplayName("haversineDistance()")
    class HaversineDistanceTests {

        @Test
        @DisplayName("Same point returns 0")
        void haversine_samePoint_returnsZero() {
            assertEquals(0.0, UtilityTools.haversineDistance(coord(55.0, 12.0), coord(55.0, 12.0)), 1e-9);
        }

        @Test
        @DisplayName("Is symmetric: dist(a,b) == dist(b,a)")
        void haversine_symmetric() {
            Coordinate a = coord(55.0, 12.0);
            Coordinate b = coord(56.0, 13.0);
            assertEquals(
                    UtilityTools.haversineDistance(a, b),
                    UtilityTools.haversineDistance(b, a),
                    1e-6
            );
        }

        @Test
        @DisplayName("Returns a positive value for two distinct points")
        void haversine_distinctPoints_positive() {
            assertTrue(UtilityTools.haversineDistance(coord(55.0, 12.0), coord(56.0, 12.0)) > 0.0);
        }

        @Test
        @DisplayName("Is never negative")
        void haversine_neverNegative() {
            assertTrue(UtilityTools.haversineDistance(coord(55.0, 12.0), coord(56.0, 13.0)) >= 0.0);
        }

        @Test
        @DisplayName("One degree of latitude at the equator is approximately 111.2 km")
        void haversine_oneDegreeLatAtEquator_approx111km() {
            double dist = UtilityTools.haversineDistance(coord(0.0, 0.0), coord(1.0, 0.0));
            assertEquals(111_195.0, dist, 1000.0);
        }

        @Test
        @DisplayName("Larger separation produces a larger distance")
        void haversine_largerSeparation_largerDistance() {
            double small = UtilityTools.haversineDistance(coord(55.0, 12.0), coord(55.1, 12.0));
            double large = UtilityTools.haversineDistance(coord(55.0, 12.0), coord(56.0, 12.0));
            assertTrue(large > small);
        }
    }
}
