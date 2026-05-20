package util;

import models.parser.LodAssigner;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class LodAssignerTest {


    //  Helpers

    private static HashMap<String, String> tags(String... pairs) {
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) map.put(pairs[i], pairs[i + 1]);
        return map;
    }

    // cosMeanLat for ~55° (Bornholm) — used wherever a real projection factor is needed
    private static final double COS_55 = Math.cos(Math.toRadians(55.0));

    // A non-zero area large enough to push areaZoom well below tagZoom
    // so that tagZoom always wins in tests that target tag-based zoom
    private static final double LARGE_AREA = 1.0;

    //  Null / empty tags

    @Nested
    @DisplayName("Null and empty tags")
    class NullAndEmptyTests {

        @Test
        @DisplayName("Null tags with zero area returns 0")
        void compute_nullTags_zeroArea_returnsZero() {
            assertEquals(0.0, LodAssigner.compute(null, 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Empty tags with zero area returns 0")
        void compute_emptyTags_zeroArea_returnsZero() {
            assertEquals(0.0, LodAssigner.compute(tags(), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Null tags with non-zero area still returns area-based zoom")
        void compute_nullTags_nonZeroArea_returnsAreaZoom() {
            double result = LodAssigner.compute(null, 0.0001, COS_55);
            assertTrue(result >= 0.0, "Area-based zoom must be non-negative");
        }
    }


    //  Buildings — fixed zoom

    @Nested
    @DisplayName("Buildings — fixed zoom 16")
    class BuildingTests {

        @Test
        @DisplayName("building tag returns fixed zoom 16")
        void compute_building_returns16() {
            assertEquals(16.0, LodAssigner.compute(tags("building", "yes"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("building:part tag returns fixed zoom 16")
        void compute_buildingPart_returns16() {
            assertEquals(16.0, LodAssigner.compute(tags("building:part", "yes"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Building zoom overrides any area-based zoom")
        void compute_building_ignoresArea() {
            // Even a huge area should not lower the zoom below 16 for buildings
            assertEquals(16.0, LodAssigner.compute(tags("building", "yes"), 100.0, COS_55), 1e-9);
        }
    }

    //  Highway tag

    @Nested
    @DisplayName("Highway types")
    class HighwayTests {

        @Test
        @DisplayName("motorway returns 7.0")
        void highway_motorway_returns7() {
            assertEquals(7.0, LodAssigner.compute(tags("highway", "motorway"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("motorway_link returns 7.0")
        void highway_motorwayLink_returns7() {
            assertEquals(7.0, LodAssigner.compute(tags("highway", "motorway_link"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("trunk returns 8.0")
        void highway_trunk_returns8() {
            assertEquals(8.0, LodAssigner.compute(tags("highway", "trunk"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("primary returns 9.0")
        void highway_primary_returns9() {
            assertEquals(9.0, LodAssigner.compute(tags("highway", "primary"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("secondary returns 10.0")
        void highway_secondary_returns10() {
            assertEquals(10.0, LodAssigner.compute(tags("highway", "secondary"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("tertiary returns 10.5")
        void highway_tertiary_returns10_5() {
            assertEquals(10.5, LodAssigner.compute(tags("highway", "tertiary"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("residential returns 13.5")
        void highway_residential_returns13_5() {
            assertEquals(13.5, LodAssigner.compute(tags("highway", "residential"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("service returns 15.0")
        void highway_service_returns15() {
            assertEquals(15.0, LodAssigner.compute(tags("highway", "service"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("footway returns 14.5")
        void highway_footway_returns14_5() {
            assertEquals(14.5, LodAssigner.compute(tags("highway", "footway"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Unknown highway type falls through to default 13.0")
        void highway_unknown_returns13() {
            assertEquals(13.0, LodAssigner.compute(tags("highway", "proposed"), 0.0, COS_55), 1e-9);
        }
    }


    //  Natural tag

    @Nested
    @DisplayName("Natural types")
    class NaturalTests {

        @Test
        @DisplayName("natural=wood returns 0.0")
        void natural_wood_returns0() {
            assertEquals(0.0, LodAssigner.compute(tags("natural", "wood"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("natural=water returns 0.0 (and uses lower pixel threshold)")
        void natural_water_returns0() {
            assertEquals(0.0, LodAssigner.compute(tags("natural", "water"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("natural=beach returns 7.0")
        void natural_beach_returns7() {
            assertEquals(7.0, LodAssigner.compute(tags("natural", "beach"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("natural=scrub returns 11.0")
        void natural_scrub_returns11() {
            assertEquals(11.0, LodAssigner.compute(tags("natural", "scrub"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("natural=rock returns 13.0")
        void natural_rock_returns13() {
            assertEquals(13.0, LodAssigner.compute(tags("natural", "rock"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Unknown natural type falls through to default 10.0")
        void natural_unknown_returns10() {
            assertEquals(10.0, LodAssigner.compute(tags("natural", "volcano"), LARGE_AREA, COS_55), 1e-9);
        }
    }


    //  Waterway tag

    @Nested
    @DisplayName("Waterway types")
    class WaterwayTests {

        @Test
        @DisplayName("waterway=river returns 10.0")
        void waterway_river_returns10() {
            assertEquals(10.0, LodAssigner.compute(tags("waterway", "river"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("waterway=stream returns 13.0")
        void waterway_stream_returns13() {
            assertEquals(13.0, LodAssigner.compute(tags("waterway", "stream"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Unknown waterway type falls through to default 14.0")
        void waterway_unknown_returns14() {
            assertEquals(14.0, LodAssigner.compute(tags("waterway", "drain"), 0.0, COS_55), 1e-9);
        }
    }


    //  Landuse tag

    @Nested
    @DisplayName("Landuse types")
    class LanduseTests {

        @Test
        @DisplayName("landuse=forest returns 0.0")
        void landuse_forest_returns0() {
            assertEquals(0.0, LodAssigner.compute(tags("landuse", "forest"), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("landuse=residential returns 8.0")
        void landuse_residential_returns8() {
            assertEquals(8.0, LodAssigner.compute(tags("landuse", "residential"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("landuse=farmland returns 11.0")
        void landuse_farmland_returns11() {
            assertEquals(11.0, LodAssigner.compute(tags("landuse", "farmland"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("landuse=grass returns 12.0")
        void landuse_grass_returns12() {
            assertEquals(12.0, LodAssigner.compute(tags("landuse", "grass"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Unknown landuse type falls through to default 10.0")
        void landuse_unknown_returns10() {
            assertEquals(10.0, LodAssigner.compute(tags("landuse", "cemetery"), LARGE_AREA, COS_55), 1e-9);
        }
    }


    //  Other top-level tags

    @Nested
    @DisplayName("Other tag types")
    class OtherTagTests {

        @Test
        @DisplayName("aeroway tag returns 8.0")
        void otherTag_aeroway_returns8() {
            assertEquals(8.0, LodAssigner.compute(tags("aeroway", "aerodrome"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("amenity tag returns 11.0")
        void otherTag_amenity_returns11() {
            assertEquals(11.0, LodAssigner.compute(tags("amenity", "parking"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("leisure tag returns 11.0")
        void otherTag_leisure_returns11() {
            assertEquals(11.0, LodAssigner.compute(tags("leisure", "park"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("man_made tag returns 13.0")
        void otherTag_manMade_returns13() {
            assertEquals(13.0, LodAssigner.compute(tags("man_made", "tower"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("tourism tag returns 13.0")
        void otherTag_tourism_returns13() {
            assertEquals(13.0, LodAssigner.compute(tags("tourism", "hotel"), LARGE_AREA, COS_55), 1e-9);
        }

        @Test
        @DisplayName("barrier tag returns 14.0")
        void otherTag_barrier_returns14() {
            assertEquals(14.0, LodAssigner.compute(tags("barrier", "fence"), LARGE_AREA, COS_55), 1e-9);
        }
    }


    //  Area-based zoom

    @Nested
    @DisplayName("Area-based zoom")
    class AreaZoomTests {

        @Test
        @DisplayName("Zero area returns 0 (roads have no area)")
        void areaZoom_zeroArea_returnsZero() {
            assertEquals(0.0, LodAssigner.compute(tags(), 0.0, COS_55), 1e-9);
        }

        @Test
        @DisplayName("Larger area produces a lower (earlier) zoom than smaller area")
        void areaZoom_largerArea_lowerZoom() {
            double smallZoom = LodAssigner.compute(tags(), 0.000001, COS_55);
            double largeZoom = LodAssigner.compute(tags(), 0.01, COS_55);
            assertTrue(largeZoom < smallZoom,
                    "A larger element must appear at a lower zoom level than a smaller one");
        }

        @Test
        @DisplayName("Tag zoom wins when it is higher than area zoom")
        void areaZoom_tagZoomWinsWhenHigher() {
            // motorway tag zoom = 7.0; large area gives areaZoom well below 7
            double result = LodAssigner.compute(tags("highway", "motorway"), LARGE_AREA, COS_55);
            assertEquals(7.0, result, 1e-9, "Tag zoom must win when it exceeds area-based zoom");
        }

        @Test
        @DisplayName("natural=water and natural=wetland use a lower pixel threshold (512)")
        void areaZoom_waterLowerThreshold_appearsEarlier() {
            // Same small area — water should appear at a lower zoom than a generic feature
            double waterZoom   = LodAssigner.compute(tags("natural", "water"),   0.000001, COS_55);
            double genericZoom = LodAssigner.compute(tags(),                      0.000001, COS_55);
            assertTrue(waterZoom < genericZoom,
                    "Water features must appear earlier (lower zoom) than generic features of the same size");
        }

        @Test
        @DisplayName("landuse tag uses a lower pixel threshold (384), appearing before generic features")
        void areaZoom_landuseLowerThreshold_appearsEarlier() {
            double landuseZoom = LodAssigner.compute(tags("landuse", "cemetery"), 0.000001, COS_55);
            double genericZoom = LodAssigner.compute(tags(),                       0.000001, COS_55);
            assertTrue(landuseZoom < genericZoom,
                    "Landuse features must appear earlier than generic features of the same size");
        }
    }
}
