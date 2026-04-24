package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.util.*;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import Interfaces.IParser;
import models.geometry.BoundingBox;
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
            InputStream is = loadResource("jsonData/" + fileName);
            if (is == null) {
                System.out.println("File " + fileName + " not found using any method");
                return;
            }
            JsonNode root = mapper.readTree(is);
            is.close();

            parseBoundingBox(root);
            parseNodes(root);

            if (boundingBox.isEmpty()) {
                computeBoundingBoxFromNodes();
            }
            parseWays(root);
            parseRelations(root);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private InputStream loadResource(String resourcePath) throws IOException {
        // Try 1: Thread context classloader
        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {
            System.out.println("Loaded " + resourcePath + " via context classloader");
            return is;
        }

        // Try 2: TestParser classloader
        is = TestParser.class.getClassLoader().getResourceAsStream(resourcePath);
        if (is != null) {
            System.out.println("Loaded " + resourcePath + " via TestParser classloader");
            return is;
        }

        // Try 3: Direct file system (for IDE testing)
        String[] possiblePaths = {
            "src/test/resources/" + resourcePath,
            System.getProperty("user.dir") + "/src/test/resources/" + resourcePath,
            System.getProperty("user.dir") + "\\src\\test\\resources\\" + resourcePath
        };

        for (String path : possiblePaths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("Loaded " + resourcePath + " from file system: " + file.getAbsolutePath());
                return new FileInputStream(file);
            }
        }

        return null;
    }

    private void parseBoundingBox(JsonNode root) {
        JsonNode bounds = root.path("meta").path("bounds");
        System.out.println("Meta exists: " + !bounds.isMissingNode());
        if (bounds.isMissingNode() || bounds.isNull()) {
            bounds = root.path("bounds");
        }

        if (!bounds.isMissingNode() && !bounds.isNull()) {

            double minLat = bounds.has("minLat") ? bounds.path("minLat").asDouble()
                                                           : bounds.path("minLat").asDouble();
            double maxLat = bounds.has("maxLat") ? bounds.path("maxLat").asDouble()
                                                           :  bounds.path("maxLat").asDouble();
            double minLon = bounds.has("minLon") ? bounds.path("minLon").asDouble()
                                                           : bounds.path("minLon").asDouble();
            double maxLon = bounds.has("maxLon") ? bounds.path("maxLon").asDouble()
                                                           :  bounds.path("maxLon").asDouble();

            boundingBox.addAll(List.of(minLat, minLon, maxLat, maxLon));
        }
    }
    private void computeBoundingBoxFromNodes() {
        if (nodeMap.isEmpty()) return;

        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;

        for (Node node: nodeMap.values()) {
            double lat = node.getLat();
            double lon = node.getLon();
            if (lat < minLat) minLat = lat;
            if (lat > maxLat) maxLat = lat;
            if (lon < minLon) minLon = lon;
            if (lon > maxLon) maxLon = lon;
        }
        boundingBox.addAll(List.of(minLat, minLon, maxLat, maxLon));
    }

    private void parseNodes(JsonNode root) {
        for (JsonNode node : root.path("nodes")) {
            long id = node.path("id").asLong();
            double lat = node.path("lat").asDouble();
            double lon = node.path("lon").asDouble();
            nodeMap.put(id, new Node(id, lat, lon));
        }
    }
    private HashMap<String, String> parseTags(JsonNode tagNode) {
        HashMap<String, String> tags = new HashMap<>();
        if (tagNode == null || tagNode.isMissingNode() || tagNode.isNull()) {
            return tags;
        }
        if (tagNode.isObject()) {
            tagNode.fields().forEachRemaining(entry -> {
                String key = entry.getKey();
                String value = entry.getValue().asText();
                value = value.replace("&amp", "&");

                tags.put(key, value);
            });
        } else if  (tagNode.isArray()) {
            for (JsonNode tag: tagNode) {
                String key = tag.path("k").asText();
                String value = tag.path("v").asText().replace("&amp", "&");
                if (!key.isEmpty()) tags.put(key, value);
            }
        }
        return tags;
    }

    private void parseWays(JsonNode root) {
        for (JsonNode way : root.path("ways")) {
            long id = way.path("id").asLong();

            JsonNode ndNode = way.path("nodes");
            if (!ndNode.isArray()) {
                System.out.println("Way " + id + " has no nodes");
                continue;
            }
            List<Node>  nodesInWay = new ArrayList<>();
            for (JsonNode ref : ndNode) {
                Node n = nodeMap.get(ref.asLong());
                if (n != null) {
                    nodesInWay.add(n);
                }
            }
            HashMap<String, String> tagsInWay = parseTags(way.path("tags"));
            wayMap.put(id, new Way(id, tagsInWay, nodesInWay));

        }
    }

    private void parseRelations(JsonNode root) {
        JsonNode relations = root.path("relations");

        Set<Long> topLevelIds = new HashSet<>();
        for (JsonNode relation : relations) {
            topLevelIds.add(relation.get("id").asLong());
        }
        for (JsonNode relation: relations) {
            long id = relation.path("id").asLong();

            List<Member> members = new ArrayList<>();
            JsonNode memberNode = relation.path("members");
            if (memberNode.isArray()) {
                for (JsonNode memberNodeEntry : memberNode) {
                    String type = memberNodeEntry.path("type").asText();
                    long ref = memberNodeEntry.path("ref").asLong();
                    String role = memberNodeEntry.path("role").asText();

                    Element element = null;
                    switch (type) {
                        case ("node"):
                                element = nodeMap.get(ref);
                                break;

                        case "way" :
                            element = wayMap.get(ref);
                            break;

                        case "relation" :
                                element = relationMap.get(ref);

                                if (element == null && topLevelIds.contains(ref)) {
                                    element = new Relation(ref, new HashMap<>(), new ArrayList<>());
                                    relationMap.put(ref, (Relation) element);
                                }
                                break;
                    }
                    if (element == null) {
                        continue;
                }
                    models.RTree.ElementType elementType;
                    try {
                        elementType = models.RTree.ElementType.valueOf(type.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        continue;
                    }
                    members.add(new Member(element, elementType, role));

            }
            HashMap<String, String> tagsInRelation = parseTags(relation.path("tags"));
            if (relationMap.containsKey(id)) {
                Relation existing = relationMap.get(id);
                existing.setMembers(members);
                existing.setTags(tagsInRelation);
            } else {
                relationMap.put(id, new Relation(id, tagsInRelation, members));
            }
            }
            relationMap.keySet().retainAll(topLevelIds);
        }
    }

    @Override
    public BoundingBox getBoundingBox() {
        if (boundingBox.size() < 4) return new BoundingBox(0,0,0,0);
        return new BoundingBox(boundingBox.get(0), boundingBox.get(1), boundingBox.get(2), boundingBox.get(3));
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
