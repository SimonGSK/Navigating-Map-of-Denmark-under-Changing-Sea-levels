package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import models.geometry.BoundingBox;
import models.parser.OsmData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import models.parser.AbstractParser;
import models.osm.Member;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import util.extensions.DependsOn;
import util.extensions.DependsOnExtension;
import util.models.ParserResults;

@ExtendWith(DependsOnExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class OsmParserTests {
    static final List<ParserResults> parserResults = new ArrayList<>();

    static {
        try {
            parserResults.add(new ParserResults("bornholm.osm", "Bornholm.json"));
        } catch (Exception e) {
            throw new  ExceptionInInitializerError("An exception occured while creating parser");

        }
    }
    @BeforeEach
    public void setUp() {
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(1)
    void ParserCreatesNodes (ParserResults pr) {
       try {
           AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
           AbstractParser<OsmData> actualParser = pr.getActualParser();

           OsmData expectedData = expectedParser.getData();
           OsmData actualData = actualParser.getData();

           HashMap<Long, Node> expectedNodes = expectedData.nodeMap();
           HashMap<Long, Node> actualNodes = actualData.nodeMap();

           assertEquals(expectedNodes.size(), actualNodes.size());
       } catch (Exception e) {
           fail(e.getMessage());
       }
    }
    
    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(2)
    @DependsOn("ParserCreatesNodes")
    void OsmNodeIdsAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Node> expectedNodes = expectedParser.getData().nodeMap();
            HashMap<Long, Node> actualNodes = actualParser.getData().nodeMap();

            for (Long nodeId: expectedNodes.keySet()) {
                assertTrue(actualNodes.containsKey(nodeId), () -> String.format("Expected node with id %s not found", nodeId));

                Node refNode = actualNodes.get(nodeId);
                assertEquals(nodeId, refNode.getId(), () -> String.format("Expected node id %s but got %s", nodeId, refNode.getId()));
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(3)
    @DependsOn("OsmNodeIdsAreCorrect")
    public void OsmNodeCoordinatesAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Node> expectedNodes = expectedParser.getData().nodeMap();
            HashMap<Long,  Node> actualNodes = actualParser.getData().nodeMap();

            for(Long nodeId: expectedNodes.keySet()) {
                Node expectedNode = expectedNodes.get(nodeId);
                Node actualNode = actualNodes.get(nodeId);

                assertEquals(expectedNode.getLat(), actualNode.getLat(), () -> String.format("Expected node with id %s to have latitude %s but got %s", nodeId, expectedNode.getLat(), actualNode.getLat()));
                assertEquals(expectedNode.getLon(), actualNode.getLon(), () -> String.format("Expected node with id %s to have longitude %s but got %s", nodeId, expectedNode.getLon(), actualNode.getLon()));
            }
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource (providerKey)
    @Order(4)
    void ParserCreatesWays(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Way> expectedWays = expectedParser.getData().wayMap();
            HashMap<Long, Way> actualWays = actualParser.getData().wayMap();

            assertEquals(expectedWays.size(), actualWays.size());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(5)
    @DependsOn("ParserCreatesWays")
    public void OsmWayIdsAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Way> expectedWays = expectedParser.getData().wayMap();
            HashMap<Long, Way> actualWays = actualParser.getData().wayMap();

            for(Map.Entry<Long, Way> entry: expectedWays.entrySet()) {
                assertTrue(actualWays.containsKey(entry.getKey()), () -> String.format("Expected way with id %s not found", entry.getKey()));

                Way actual = actualWays.get(entry.getKey());
                assertEquals(entry.getKey(), actual.getId(), () -> String.format("[ERROR - %s] : ID mismatch between key-value pair in OsmWayMap. Key %d mapped to object with id %d", pr.getName(), entry.getKey(), actual.getId()));
            }
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(6)
    @DependsOn("ParserCreatesWays")
    public void OsmWayNodeReferencesAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Way> expectedWays = expectedParser.getData().wayMap();
            HashMap<Long, Way> actualWays = actualParser.getData().wayMap();

            for(Map.Entry<Long, Way> entry: expectedWays.entrySet()) {
                Way actual = actualWays.get(entry.getKey());
                Set<Long> actualNodeIds = actual.getNodes().stream().map(Node::getId).collect(Collectors.toSet());

                for(Node node: entry.getValue().getNodes()) {
                    assertTrue(actualNodeIds.contains(node.getId()), () -> String.format("[ERROR - %s] : Node #%d is missing on Way #%d", pr.getName(), node.getId(), entry.getKey()));
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(7)
    @DependsOn("ParserCreatesWays")
    public void OsmWayTagsAreCorrect(ParserResults pr) {
         try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Way> expectedWays = expectedParser.getData().wayMap();
            HashMap<Long, Way> actualWays = actualParser.getData().wayMap();

            for(Way way:expectedWays.values()) {
                HashMap<String, String> expectedTags = way.getTags();
                HashMap<String, String> actualTags = actualWays.get(way.getId()).getTags();

                assertEquals(expectedTags.size(), actualTags.size(), () -> String.format("[ERROR - %s] : Tag count mismatch for Way #%d. Expected %d tags but got %d", pr.getName(), way.getId(), expectedTags.size(), actualTags.size()));

                for(String tag:  expectedTags.keySet()) {
                    assertTrue(actualTags.containsKey(tag), () -> String.format("[ERROR - %s] : Tag key '%s' not present on Way #%d", pr.getName(), tag, way.getId()));
                    assertEquals(expectedTags.get(tag), actualTags.get(tag), () -> String.format("[ERROR - %s] : On Way #%d, tag key '%s' has value '%s', should be '%s'", pr.getName(), way.getId(), tag, actualTags.get(tag), expectedTags.get(tag)));
                }
            }
         } catch(Exception e) {
             fail(e.getMessage());
         }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(8)
    public void ParserCreatesRelations(ParserResults pr) {
       try {
           AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
           AbstractParser<OsmData> actualParser = pr.getActualParser();

           HashMap<Long, Relation> expectedRelations = expectedParser.getData().relationMap();
           HashMap<Long, Relation> actualRelations = actualParser.getData().relationMap();

           assertEquals(expectedRelations.size(), actualRelations.size());
       } catch (Exception e) {
           fail(e.getMessage());
       }
    }
    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(9)
    @DependsOn("ParserCreatesRelations")
    public void OsmRelationIdsAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Relation> expectedRelations = expectedParser.getData().relationMap();
            HashMap<Long, Relation> actualRelations = actualParser.getData().relationMap();

            for(Relation expected: expectedRelations.values()) {
                assertTrue(actualRelations.containsKey(expected.getId()), () -> String.format("[ERROR - %s] : Relation #%d was missing in OsmRelationMap", pr.getName(), expected.getId()));

                Relation actual = actualRelations.get(expected.getId());
                assertEquals(expected.getId(), actual.getId(), () -> String.format("[ERROR - %s] : ID mismatch between key-value pair in OsmRelationMap. Key %d mapped to object with id %d", pr.getName(), expected.getId(), actual.getId()));
            }
        } catch  (Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(10)
    @DependsOn("ParserCreatesRelations")
    public void OsmRelationTagsAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Relation> expectedRelations = expectedParser.getData().relationMap();
            HashMap<Long, Relation> actualRelations = actualParser.getData().relationMap();

            for(Relation expectedRelation: expectedRelations.values()) {
                Relation actualRelation = actualRelations.get(expectedRelation.getId());

                HashMap<String, String> expectedTags = expectedRelation.getTags();
                HashMap<String, String> actualTags = actualRelation.getTags();
                assertEquals(expectedTags.size(), actualTags.size());

                for(String tag:  expectedTags.keySet()) {
                    assertTrue(actualTags.containsKey(tag), () -> String.format("[ERROR - %s] : Tag key '%s' not present on Relation #%d", pr.getName(), tag, expectedRelation.getId()));
                    assertEquals(expectedTags.get(tag), actualTags.get(tag), () -> String.format("[ERROR - %s] : On Relation #%d, tag key '%s' has value '%s', should be '%s'", pr.getName(), expectedRelation.getId(), tag, actualTags.get(tag), expectedTags.get(tag)));
                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(11)
    @DependsOn("ParserCreatesRelations")
    public void OsmRelationMembersAreCorrect(ParserResults pr) {
        try {
            AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
            AbstractParser<OsmData> actualParser = pr.getActualParser();

            HashMap<Long, Relation> expectedRelations = expectedParser.getData().relationMap();
            HashMap<Long, Relation> actualRelations = actualParser.getData().relationMap();

            for(Relation expectedRelation: expectedRelations.values()) {
                Relation actualRelation = actualRelations.get(expectedRelation.getId());
                List<Member> expectedMembers = expectedRelation.getMembers();
                List<Member> actualMembers = actualRelation.getMembers();

                for(Member member: expectedMembers) {
                    Element memberElement = member.getElement();

                    assertTrue(actualMembers.stream().anyMatch(m -> m.getElement().getId() == memberElement.getId() && m.getElement().getClass().equals(memberElement.getClass()) && m.getRole().equals(member.getRole())), () -> String.format("[ERROR - %s] : Member with id %d and role '%s' was missing on Relation #%d", pr.getName(), memberElement.getId(), member.getRole(), expectedRelation.getId()));

                }
            }
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
    @ParameterizedTest
    @MethodSource(providerKey)
    @Order(12)
    void BoundingBoxIsCorrect(ParserResults pr) {
       try {
           AbstractParser<OsmData> expectedParser = pr.getExpectedParser();
           AbstractParser<OsmData> actualParser = pr.getActualParser();

           BoundingBox actualBounds = actualParser.getData().bounds();
           BoundingBox expectedBounds = expectedParser.getData().bounds();

           double expectedMinLat = expectedBounds.minLat();
           double expectedMinLon = expectedBounds.minLon();
           double expectedMaxLat = expectedBounds.maxLat();
           double expectedMaxLon = expectedBounds.maxLon();
           double actualMinLat = actualBounds.minLat();
           double actualMinLon = actualBounds.minLon();
           double actualMaxLat = actualBounds.maxLat();
           double actualMaxLon = actualBounds.maxLon();

           assertEquals(expectedMinLat, actualMinLat, () -> String.format("[ERROR - %s] : Minimum latitude should be %f, was %f", pr.getName(), expectedMinLat, actualMinLat));
           assertEquals(expectedMaxLat, actualMaxLat, () -> String.format("[ERROR - %s] : Maximum latitude should be %f, was %f", pr.getName(), expectedMaxLat, actualMaxLat));
           assertEquals(expectedMinLon, actualMinLon, () -> String.format("[ERROR - %s] : Minimum longitude should be %f, was %f", pr.getName(), expectedMinLon, actualMinLon));
           assertEquals(expectedMaxLon, actualMaxLon, () -> String.format("[ERROR - %s] : Maximum longitude should be %f, was %f", pr.getName(), expectedMaxLon, actualMaxLon));
       } catch (Exception e) {
           fail(e.getMessage());
       }
    }
    final String providerKey = "parserResultsProvider";
    static Stream<ParserResults> parserResultsProvider() {
        return parserResults.stream();
    }
}
