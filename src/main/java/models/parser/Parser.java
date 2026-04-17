package models.parser;

import Interfaces.IParser;
import models.geometry.BoundingBox;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static models.geometry.BoundingBox.computeMbr;

public class Parser implements IParser {
    private final String fileName;
    private final HashMap<Long, Node> nodeMap = new HashMap<>();
    private final HashMap<Long, Way> wayMap = new HashMap<>();
    private final HashMap<Long, Relation> relationMap = new HashMap<>();
    private BoundingBox mbr;

    public Parser(String filename) {
        this.fileName = filename;
    }

    @Override
    public void parse() {
        try {
            InputStream is = Parser.class.getResourceAsStream("/data/" + fileName);
            if (is == null) {
                throw new IOException("OSM resource not found: /data/" + fileName + " (check src/main/resources path)");
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;

            while ((line = br.readLine()) != null) {
                if (line.contains("<bounds")) {
                    mbr = extractBounds(line);
                } else if (line.contains("<node") && !line.contains("</node")) {
                    Node node = extractNode(line);
                    nodeMap.put(node.getId(), node);
                } else if (line.contains("<way")) {
                    Way way = extractWay(line, br);
                    wayMap.put(way.getId(), way);
                } else if (line.contains("<relation ") || line.contains("<relation>")) {
                    extractRelation(line, br);
                }
            }

            if (mbr == null) {
                mbr = computeMbr(nodeMap.values().stream().toList());
            }

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private void extractRelation(String line, BufferedReader br) throws IOException {
        List<Member> members = new ArrayList<>();
        HashMap<String, String> tags = new HashMap<>();
        long relationID = getAttributeLong(line, "id");

        while (!line.contains("</relation>")) {
            line = br.readLine().trim();

            if (line.contains("<member")) {
                String type = getAttribute(line, "type");
                Long ref = getAttributeLong(line, "ref");
                String role = getAttribute(line, "role");

                switch (type) {
                    case "node" -> {
                        Node node = nodeMap.get(ref);
                        if (node != null) {
                            members.add(new Member(node, role));
                        }
                    }
                    case "way" -> {
                        Way way =  wayMap.get(ref);
                        if (way == null) {
                            way = new Way(ref, new HashMap<>(), new ArrayList<>());
                            wayMap.put(ref, way);
                        }
                        members.add(new Member(way, role));
                    }
                    case "relation" -> {
                        if (!relationMap.containsKey(ref)) {
                            relationMap.put(ref, new Relation(ref, new HashMap<>(), new ArrayList<>()));
                        }
                        members.add(new Member(relationMap.get(ref), role));
                    }
                }
            } else if (line.contains("<tag")) {
                String k = getAttribute(line, "k");
                String v = getAttribute(line, "v");
                tags.put(k, v);
            }
        }

        if (relationMap.containsKey(relationID)) {
            Relation existing = relationMap.get(relationID);
            existing.setMembers(members);
            existing.setTags(tags);
            return;
        }
        Relation relation = new Relation(relationID, tags, members);
        relationMap.put(relationID, relation);
    }

    private Way extractWay(String line, BufferedReader br) throws IOException {
        List<Node> nodes = new ArrayList<>();
        HashMap<String, String> tags = new HashMap<>();

        long wayID = getAttributeLong(line, "id");

        while (!line.contains("</way>")) {
            line = br.readLine().trim();
            if (line.contains("<nd")) {
                long ndID = getAttributeLong(line, "ref");
                nodes.add(getOsmNodeMap().get(ndID));
            }
            if (line.contains("<tag")) {
                String k = getAttribute(line, "k");
                String v = getAttribute(line, "v");
                tags.put(k, v);
            }
        }

        return new Way(wayID, tags, nodes);
    }

    private Node extractNode(String line) {
        double lat = getAttributeDouble(line, "lat");
        double lon = getAttributeDouble(line, "lon");
        long id = getAttributeLong(line, "id");
        return new Node(id, lat, lon); // TODO: Parse tags for Node and add as input
    }

    private BoundingBox extractBounds(String line) {
        double minLat = getAttributeDouble(line, "minlat");
        double minLon = getAttributeDouble(line, "minlon");
        double maxLat = getAttributeDouble(line, "maxlat");
        double maxLon = getAttributeDouble(line, "maxlon");

        return new BoundingBox(minLon, minLat, maxLon, maxLat);
    }

    public String getAttribute(String s, String key) {
        String pattern = key + "=\"";
        int start = s.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int valueStart = start + pattern.length();
        int valueEnd = s.indexOf('"', valueStart);
        String value =  s.substring(valueStart, valueEnd);
        return value
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");
    }

    public double getAttributeDouble(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return Double.NaN;
        }
        return Double.parseDouble(val);
    }

    public long getAttributeLong(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return 0L;
        }
        return Long.parseLong(val);
    }

    //DO NOT MODIFY BELOW GETTER METHODS
    @Override
    public BoundingBox getBoundingBox() {
        return mbr;
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
