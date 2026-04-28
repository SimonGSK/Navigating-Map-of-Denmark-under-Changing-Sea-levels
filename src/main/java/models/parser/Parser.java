package models.parser;

import Interfaces.IParser;
import models.RTree.ElementType;
import models.RTree.EntryKey;
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
                        if (nodeMap.containsKey(ref)) {
                            Member member = new Member(nodeMap.get(ref), ElementType.node, role);
                            members.add(member);
                        }
                    }
                    case "way" -> {
                        if (wayMap.containsKey(ref)) {
                            Member member = new Member(wayMap.get(ref), ElementType.way, role);
                            members.add(member);
                        }
                    }
                    case "relation" -> {
                        if (!relationMap.containsKey(ref)) {
                            relationMap.put(ref, new Relation(ref, new HashMap<>(), new ArrayList<>()));
                        }
                        Member member = new Member(relationMap.get(ref), ElementType.relation, role);
                        members.add(member);
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
            existing.setMinZoomLevel(calculateZoomLevelForRelation(tags));
            return;
        }
        Relation relation = new Relation(relationID, tags, members);
        relation.setMinZoomLevel(calculateZoomLevelForRelation(tags));
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

        Way way = new Way(wayID, tags, nodes);
        way.setMinZoomLevel(calculateZoomLevelForWay(tags));
        return way;
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

        return new BoundingBox(minLat, minLon, maxLat, maxLon);
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

    //TODO: Måske også bruge area til at forbedre LOD

    private double calculateZoomLevelForWay(HashMap<String, String> tags) {
        if (tags == null) return 0;

        String highway = tags.get("highway");
        if (highway != null) {
            return switch (highway) {
                case "motorway", "motorway_link",
                     "trunk",    "trunk_link"       -> 8.0;
                case "primary",   "primary_link"    -> 10.0;
                case "secondary", "secondary_link"  -> 11.0;
                case "tertiary",  "tertiary_link"   -> 12.0;
                case "residential", "unclassified",
                     "living_street"                -> 13.0;
                case "service", "track"             -> 14.0;
                case "path", "footway", "cycleway",
                     "steps", "bridleway"           -> 14.5;
                default -> 13.0;
            };
        }

        if (tags.containsKey("building") || tags.containsKey("building:part")) return 14.0;

        String waterway = tags.get("waterway");
        if (waterway != null) {
            return switch (waterway) {
                case "river", "canal" -> 0.0; // Store vandveje altid synlige
                case "stream"         -> 12.0;
                default               -> 13.0;
            };
        }

        String landuse = tags.get("landuse");
        if (landuse != null) {
            return switch (landuse) {
                case "forest", "grass",
                     "farmland", "farmyard" -> 0.0; // Baggrundslandskab altid synligt
                case "industrial"           -> 9.0;
                default                     -> 0.0;
            };
        }

        // Naturlag — kystlinje og store naturområder SKAL altid renderes
        if (tags.containsKey("natural"))   return 0.0;
        if (tags.containsKey("aeroway"))   return 10.0;
        if (tags.containsKey("amenity")
                || tags.containsKey("leisure"))   return 11.0;
        if (tags.containsKey("man_made"))  return 13.0;
        if (tags.containsKey("tourism")
                || tags.containsKey("historic"))  return 13.0;
        if (tags.containsKey("barrier"))   return 14.0;

        return 0.0; // Fallback: vis hellere for meget end for lidt
    }

    private double calculateZoomLevelForRelation(HashMap<String, String> tags) {
        if (tags == null) return 0;

        String natural = tags.get("natural");
        if (natural != null) return 0.0; // Kystlinje, vand, strand — altid synlig

        String landuse = tags.get("landuse");
        if (landuse != null) {
            return switch (landuse) {
                case "forest", "grass",
                     "farmland"          -> 0.0; // Baggrundslandskab
                case "industrial"        -> 9.0;
                default                  -> 0.0;
            };
        }

        if (tags.containsKey("building") || tags.containsKey("building:part")) return 14.0;
        if (tags.containsKey("amenity") || tags.containsKey("leisure"))        return 11.0;

        return 0.0;
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
