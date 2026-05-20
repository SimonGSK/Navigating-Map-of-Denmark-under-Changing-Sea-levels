package util;

import models.osm.Node;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class OsmElementTest {


    //  Helpers

    /** Node with null tags (default after construction). */
    private static Node bare() {
        return new Node(1L, 0.0, 0.0);
    }

    /** Node whose tags are set to the given key-value pairs. */
    private static Node nodeWithTags(String... pairs) {
        Node n = new Node(1L, 0.0, 0.0);
        HashMap<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) map.put(pairs[i], pairs[i + 1]);
        n.setTags(map);
        return n;
    }


    //  getTags()

    @Nested
    @DisplayName("getTags()")
    class GetTagsTests {

        @Test
        @DisplayName("Returns null when tags are null")
        void getTags_nullTags_returnsNull() {
            assertNull(bare().getTags());
        }

        @Test
        @DisplayName("Returns a defensive copy — not the same map reference")
        void getTags_returnsDefensiveCopy_notSameReference() {
            Node n = nodeWithTags("highway", "primary");
            assertNotSame(n.getTags(), n.getTags());
        }

        @Test
        @DisplayName("Modifying the returned map does not affect the original tags")
        void getTags_modifyingCopy_doesNotAffectOriginal() {
            Node n = nodeWithTags("highway", "primary");
            n.getTags().put("extra", "value");
            assertNull(n.getTag("extra"), "Mutation of defensive copy must not change stored tags");
        }

        @Test
        @DisplayName("Returned map contains the expected entries")
        void getTags_containsExpectedEntries() {
            Node n = nodeWithTags("highway", "primary", "name", "Main St");
            HashMap<String, String> tags = n.getTags();
            assertEquals("primary", tags.get("highway"));
            assertEquals("Main St", tags.get("name"));
        }
    }


    //  getTag()

    @Nested
    @DisplayName("getTag()")
    class GetTagTests {

        @Test
        @DisplayName("Returns null when tags are null")
        void getTag_nullTags_returnsNull() {
            assertNull(bare().getTag("highway"));
        }

        @Test
        @DisplayName("Returns the value for an existing key")
        void getTag_existingKey_returnsValue() {
            assertEquals("primary", nodeWithTags("highway", "primary").getTag("highway"));
        }

        @Test
        @DisplayName("Returns null for a key that is not present")
        void getTag_missingKey_returnsNull() {
            assertNull(nodeWithTags("highway", "primary").getTag("building"));
        }
    }


    //  getColor() — null-return (not-to-draw) cases

    @Nested
    @DisplayName("getColor() — returns null (not drawn)")
    class ColorNullTests {

        @Test
        @DisplayName("power key returns null")
        void getColor_powerKey_returnsNull() {
            assertNull(nodeWithTags("power", "line").getColor());
        }

        @Test
        @DisplayName("route=power returns null")
        void getColor_routePower_returnsNull() {
            assertNull(nodeWithTags("route", "power").getColor());
        }

        @Test
        @DisplayName("boundary key returns null")
        void getColor_boundaryKey_returnsNull() {
            assertNull(nodeWithTags("boundary", "administrative").getColor());
        }

        @Test
        @DisplayName("tag value 'boundary' returns null")
        void getColor_boundaryValue_returnsNull() {
            assertNull(nodeWithTags("admin_level", "boundary").getColor());
        }

        @Test
        @DisplayName("tag value 'ferry' returns null")
        void getColor_ferryValue_returnsNull() {
            assertNull(nodeWithTags("route", "ferry").getColor());
        }

        @Test
        @DisplayName("location=underwater returns null")
        void getColor_locationUnderwater_returnsNull() {
            assertNull(nodeWithTags("location", "underwater").getColor());
        }

        @Test
        @DisplayName("natural=sea returns null")
        void getColor_naturalSea_returnsNull() {
            assertNull(nodeWithTags("natural", "sea").getColor());
        }

        @Test
        @DisplayName("natural=bay returns null")
        void getColor_naturalBay_returnsNull() {
            assertNull(nodeWithTags("natural", "bay").getColor());
        }

        @Test
        @DisplayName("seamark:type key returns null")
        void getColor_seamarkType_returnsNull() {
            assertNull(nodeWithTags("seamark:type", "harbour").getColor());
        }

        @Test
        @DisplayName("barrier key returns null")
        void getColor_barrierKey_returnsNull() {
            assertNull(nodeWithTags("barrier", "fence").getColor());
        }

        @Test
        @DisplayName("man_made=pipeline returns null")
        void getColor_manMadePipeline_returnsNull() {
            assertNull(nodeWithTags("man_made", "pipeline").getColor());
        }

        @Test
        @DisplayName("demolished:building key returns null")
        void getColor_demolishedBuilding_returnsNull() {
            assertNull(nodeWithTags("demolished:building", "yes").getColor());
        }
    }


    //  getColor() — positive colour returns

    @Nested
    @DisplayName("getColor() — colour values")
    class ColorValueTests {

        @Test
        @DisplayName("Null tags returns BLACK")
        void getColor_nullTags_returnsBlack() {
            assertEquals(Color.BLACK, bare().getColor());
        }

        @Test
        @DisplayName("waterway key returns dusty blue #A5BFD2")
        void getColor_waterwayKey_returnsDustyBlue() {
            assertEquals(Color.decode("#A5BFD2"), nodeWithTags("waterway", "river").getColor());
        }

        @Test
        @DisplayName("natural=water returns dusty blue #A5BFD2")
        void getColor_naturalWater_returnsDustyBlue() {
            assertEquals(Color.decode("#A5BFD2"), nodeWithTags("natural", "water").getColor());
        }

        @Test
        @DisplayName("natural=wood returns forest green #A8C19D")
        void getColor_naturalWood_returnsForestGreen() {
            assertEquals(Color.decode("#A8C19D"), nodeWithTags("natural", "wood").getColor());
        }

        @Test
        @DisplayName("natural=rock returns grey #C8C8C8")
        void getColor_naturalRock_returnsGrey() {
            assertEquals(Color.decode("#C8C8C8"), nodeWithTags("natural", "rock").getColor());
        }

        @Test
        @DisplayName("natural=wetland returns swamp colour #B9C5B2")
        void getColor_naturalWetland_returnsSwamp() {
            assertEquals(Color.decode("#B9C5B2"), nodeWithTags("natural", "wetland").getColor());
        }

        @Test
        @DisplayName("natural=beach returns sand colour #E5DCC6")
        void getColor_naturalBeach_returnsSand() {
            assertEquals(Color.decode("#E5DCC6"), nodeWithTags("natural", "beach").getColor());
        }

        @Test
        @DisplayName("Unknown natural type returns standard nature green #D3DFC5")
        void getColor_naturalUnknown_returnsStandardGreen() {
            assertEquals(Color.decode("#D3DFC5"), nodeWithTags("natural", "cliff").getColor());
        }

        @Test
        @DisplayName("highway (non-track) returns white #FFFFFF")
        void getColor_highwayPrimary_returnsWhite() {
            assertEquals(Color.decode("#FFFFFF"), nodeWithTags("highway", "primary").getColor());
        }

        @Test
        @DisplayName("highway=track returns light brown #C9BEB0")
        void getColor_highwayTrack_returnsLightBrown() {
            assertEquals(Color.decode("#C9BEB0"), nodeWithTags("highway", "track").getColor());
        }

        @Test
        @DisplayName("landuse=forest returns green #9DBA8E")
        void getColor_landuseForest_returnsGreen() {
            assertEquals(Color.decode("#9DBA8E"), nodeWithTags("landuse", "forest").getColor());
        }

        @Test
        @DisplayName("landuse=grass returns green #D3DFC5")
        void getColor_landuseGrass_returnsGreen() {
            assertEquals(Color.decode("#D3DFC5"), nodeWithTags("landuse", "grass").getColor());
        }

        @Test
        @DisplayName("landuse=industrial returns grey #DBD7D2")
        void getColor_landuseIndustrial_returnsGrey() {
            assertEquals(Color.decode("#DBD7D2"), nodeWithTags("landuse", "industrial").getColor());
        }

        @Test
        @DisplayName("landuse=residential returns beige #E3E1DA")
        void getColor_landuseResidential_returnsBeige() {
            assertEquals(Color.decode("#E3E1DA"), nodeWithTags("landuse", "residential").getColor());
        }

        @Test
        @DisplayName("Unknown landuse returns default #D1D1C4")
        void getColor_landuseUnknown_returnsDefault() {
            assertEquals(Color.decode("#D1D1C4"), nodeWithTags("landuse", "cemetery").getColor());
        }

        @Test
        @DisplayName("aeroway=runway returns grey-blue #B0B8C1")
        void getColor_aerowayRunway_returnsGreyBlue() {
            assertEquals(Color.decode("#B0B8C1"), nodeWithTags("aeroway", "runway").getColor());
        }

        @Test
        @DisplayName("Unknown aeroway returns light grey-blue #D1D9E0")
        void getColor_aerowayUnknown_returnsLightGreyBlue() {
            assertEquals(Color.decode("#D1D9E0"), nodeWithTags("aeroway", "terminal").getColor());
        }

        @Test
        @DisplayName("amenity=park returns green #C7D9B8")
        void getColor_amenityPark_returnsGreen() {
            assertEquals(Color.decode("#C7D9B8"), nodeWithTags("leisure", "park").getColor());
        }

        @Test
        @DisplayName("amenity=hospital returns dusty pink #EAD7D7")
        void getColor_amenityHospital_returnsDustyPink() {
            assertEquals(Color.decode("#EAD7D7"), nodeWithTags("amenity", "hospital").getColor());
        }

        @Test
        @DisplayName("Unknown amenity returns beige #D9D2C5")
        void getColor_amenityUnknown_returnsBeige() {
            assertEquals(Color.decode("#D9D2C5"), nodeWithTags("amenity", "pharmacy").getColor());
        }

        @Test
        @DisplayName("building key returns dusty orange #D2B4A4")
        void getColor_buildingKey_returnsDustyOrange() {
            assertEquals(Color.decode("#D2B4A4"), nodeWithTags("building", "yes").getColor());
        }

        @Test
        @DisplayName("man_made (non-pipeline) returns #BDB9B5")
        void getColor_manMadeNonPipeline_returnsColor() {
            assertEquals(Color.decode("#BDB9B5"), nodeWithTags("man_made", "tower").getColor());
        }

        @Test
        @DisplayName("tourism key returns muted brown #C9BFA9")
        void getColor_tourismKey_returnsMutedBrown() {
            assertEquals(Color.decode("#C9BFA9"), nodeWithTags("tourism", "hotel").getColor());
        }

        @Test
        @DisplayName("Unrecognised tags return fallback colour #F2F0E9")
        void getColor_unknownTags_returnsFallback() {
            assertEquals(Color.decode("#F2F0E9"), nodeWithTags("unknown_key", "unknown_value").getColor());
        }
    }


    //  getColor() — caching

    @Nested
    @DisplayName("getColor() — caching")
    class ColorCachingTests {

        @Test
        @DisplayName("Returns the same Color object on repeated calls")
        void getColor_repeatedCalls_returnsSameObject() {
            Node n = nodeWithTags("building", "yes");
            assertSame(n.getColor(), n.getColor(), "getColor() must return the cached instance on every call");
        }

        @Test
        @DisplayName("Null-tags result (BLACK) is also cached")
        void getColor_nullTagsCached_returnsSameObject() {
            Node n = bare();
            assertSame(n.getColor(), n.getColor());
        }
    }
}
