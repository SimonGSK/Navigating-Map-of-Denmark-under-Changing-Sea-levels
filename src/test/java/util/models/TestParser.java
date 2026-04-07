package util.models;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import Interfaces.IParser;
import models.osm.Member;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

public class TestParser implements IParser {
    String fileName;
    private final List<Double> boundingBox = new ArrayList<>();
    private final HashMap<Long, Node> nodeMap = new HashMap<>();
    private final HashMap<Long, Way> wayMap = new HashMap<>();
    private final HashMap<Long, Relation> relationMap = new HashMap<>();

    ObjectMapper mapper = new ObjectMapper();

    public TestParser(String filename) {
        this.fileName = filename;
    }

    @Override
    public void parse() {
    try {
        InputStream is = TestParser.class.getClassLoader().getResourceAsStream("jsonData/" + fileName);
        if (is == null) {
            System.out.println("File " + fileName + " not found");
            return;
        }
        JsonNode root = mapper.readTree(is);

        parseBoundingBox(root);
        parseNodes(root);
        parseWays(root);
        parseRelations(root);
    } catch (IOException e) {
        System.out.println(e.getMessage());
    }
    }

    private void parseBoundingBox(JsonNode root) {
        JsonNode bounds = root.path("osm").path("bounds");

        double minLat = bounds.path("@minLat").asDouble();
        double maxLat = bounds.path("@maxLat").asDouble();
        double minLon = bounds.path("@minLon").asDouble();
        double maxLon = bounds.path("@maxLon").asDouble();

        boundingBox.addAll(List.of(minLat, minLon, maxLat, maxLon));

    }

    private void parseNodes(JsonNode root) {
        JsonNode nodes =root.path("osm").path("node");

        for (JsonNode node : nodes) {
            long id = node.path("@id").asLong();
            double lat = node.path("@lat").asDouble();
            double lon = node.path("@lon").asDouble();

            nodeMap.put(id, new Node(id, lat, lon));
        }
    }

    private void parseWays(JsonNode root) {
        JsonNode ways = root.path("osm").path("way");
        for (JsonNode way : ways) {
            long id = way.path("@id").asLong();

            List<Node> nodesInWay = new ArrayList<>();
            JsonNode ndNode = way.path("nd");
            if (!ndNode.isArray()) {
                System.out.println("Way " + id + " has no nodes");
                continue;
            }
            for (JsonNode node : ndNode) {
                long nodeRef = node.path("@ref").asLong();
                Node n = nodeMap.get(nodeRef);
                if (n != null) {
                    nodesInWay.add(n);
                }
            }
            HashMap<String, String> tagsInWay = new  HashMap<>();
            JsonNode tagNode = way.path("tag");

            ArrayNode tagArray = mapper.createArrayNode();
            if (tagNode.isArray()) {
                tagArray = (ArrayNode) tagNode;
            } else {
                tagArray.add(tagNode);
            }
            for (JsonNode tag : tagArray) {
                String key = tag.path("@k").asText();
                String value = tag.path("@v").asText();
                tagsInWay.put(key, value);
            }
                wayMap.put(id, new Way(id, nodesInWay, tagsInWay));
        }
    }

    private void parseRelations(JsonNode root) {
        JsonNode relations = root.path("osm").path("relation");

        for (JsonNode relation: relations) {
            long id = relation.path("@id").asLong();

            List<Member> members = new ArrayList<>();
            JsonNode memberNode = relation.path("member");
            if (!memberNode.isArray()) {
                System.out.println("Relation " + id + " has no members");
                continue;
            }
            for (JsonNode member : memberNode) {
                String type = member.path("@type").asText();
                long ref = member.path("@ref").asLong();
                String role = member.path("@role").asText();

                Element element = null;
                switch (type) {
                    case ("node") -> {
                        if (!wayMap.containsKey(ref)) {
                            continue;
                        }
                        element = nodeMap.get(ref);
                    }
                    case "way"  -> {
                        if (!wayMap.containsKey(ref)) {
                            continue;
                        }
                        element = wayMap.get(ref);
                    }
                    case "relation" -> {
                        boolean relationExists = relationMap.containsKey(ref);
                        if (relationExists) {
                            element = relationMap.get(ref);
                        } else {
                            Relation relation1 = new Relation(ref, List.of(), new HashMap());
                            relationMap.put(ref, relation1);
                            element = relation1;
                        }
                    }
                }
                members.add(new Member(element, role));
            }
            HashMap<String, String> tagsInRelation = new  HashMap<>();
            JsonNode tagNode = relation.path("tag");
            if (!tagNode.isArray()) {
                System.out.println("Relation " + id + " has no tags");
                continue;
            }
            for (JsonNode tag : tagNode) {
                String key = tag.path("@k").asText();
                String value = tag.path("@v").asText();
                tagsInRelation.put(key, value);
            }
            boolean relationExists = relationMap.containsKey(id);
            if (relationExists) {
                Relation relation1 = relationMap.get(id);
                relation1.setMembers(members);
                relation1.setTags(tagsInRelation);
            } else {
                relationMap.put(id, new Relation(id, members, tagsInRelation));
            }
        }
    }

    @Override
    public List<Double> getBoundingBox() {
        return boundingBox;
    }
    @Override
    public HashMap<Long, Node> getOsmNodeMap() {
        return nodeMap;
    }
    @Override
    public HashMap<Long, Way> getOsmWayMap() {
        return wayMap;
    }
    @Override
    public HashMap<Long, Relation> getOsmRelationMap() {
        return relationMap;
    }
}
