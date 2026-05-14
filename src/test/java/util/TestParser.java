package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import models.parser.AbstractParser;
import enums.ElementType;
import models.geometry.BoundingBox;
import models.osm.Member;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import models.parser.OsmData;
import models.ui.AppData;

import static java.lang.reflect.Array.getDouble;
import static models.geometry.BoundingBox.computeMbr;


public class TestParser extends AbstractParser<OsmData> {
    String fileName;
    private BoundingBox boundingBox;
    private final HashMap<Long, Node> nodeMap = new HashMap<>();
    private final HashMap<Long, Way> wayMap = new HashMap<>();
    private final HashMap<Long, Relation> relationMap = new HashMap<>();

    private final ObjectMapper mapper = new ObjectMapper();

    public TestParser(String filename) {
        this.fileName = filename;
    }

    @Override
    public void parse(String filePath) throws IOException {
        this.fileName = filePath;
        InputStream is = loadResource("jsonData/" + filePath);
        if (is == null) {
            throw new IOException("File " + filePath + " not found");
        }
        JsonNode root = mapper.readTree(is);
        is.close();

        parseBoundingBox(root);
        parseNodes(root);

        if (boundingBox == null) {
            boundingBox = computeMbr(nodeMap.values().stream().toList());
        }
        parseWays(root);
        parseRelations(root);

        data = new OsmData(boundingBox, nodeMap, wayMap, relationMap);
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

        if (bounds.isMissingNode() || bounds.isNull()) {
            return;
        }

          double minLat = getDouble(bounds, "minlat", "minLat");
          double minLon = getDouble(bounds, "minlon", "minLon");
          double maxLat = getDouble(bounds, "maxlat", "maxLat");
          double maxLon = getDouble(bounds, "maxlon", "maxLon");

            boundingBox = new BoundingBox(minLat, minLon, maxLat, maxLon);
        }
        private double getDouble(JsonNode node, String primaryKey, String fallbackKey) {
        if (node.has(primaryKey)) {
            return node.path(primaryKey).asDouble();
        }
        return node.path(fallbackKey).asDouble();
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
                    ElementType elementType;
                    try {
                        elementType = ElementType.valueOf(type.toUpperCase());
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


    public BoundingBox getBoundingBox() {
        if (boundingBox == null) {
            return new BoundingBox(0, 0, 0, 0);
        }
        return boundingBox;
    }

    public HashMap<Long, Node> getOsmNodeMap() {
        return nodeMap;
    }

    public HashMap<Long, Way> getOsmWayMap() {
        return wayMap;
    }

    public HashMap<Long, Relation> getOsmRelationMap() {
        return relationMap;
    }

}
