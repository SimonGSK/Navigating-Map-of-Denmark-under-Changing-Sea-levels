package models.parser;

import enums.ElementType;
import models.geometry.BoundingBox;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static models.geometry.BoundingBox.computeMbr;

public class OsmParser extends AbstractParser<OsmData> {
    private final HashMap<Long, Node> nodeMap = new HashMap<>();
    private final HashMap<Long, Way> wayMap = new HashMap<>();
    private final HashMap<Long, Relation> relationMap = new HashMap<>();

    private BoundingBox mbr;

    public OsmParser(String absoluteFilePath) throws IOException {
        parse(absoluteFilePath);
    }

    public void parse(String filePath) throws IOException {
        this.filePath = filePath;

        try {
            InputStream inputStream = new FileInputStream(filePath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (line.contains("<bounds")) {
                    mbr = extractBounds(line);
                } else if (line.contains("<node") && !line.contains("</node")) {
                    Node node = extractNode(line);
                    nodeMap.put(node.getId(), node);
                } else if (line.contains("<way")) {
                    Way way = extractWay(line, bufferedReader);
                    wayMap.put(way.getId(), way);
                } else if (line.contains("<relation ") || line.contains("<relation>")) {
                    extractRelation(line, bufferedReader);
                }
            }

            if (mbr == null) {
                mbr = computeMbr(nodeMap.values().stream().toList());
            }
        } catch (FileNotFoundException fileNotFoundException) {
            throw new FileNotFoundException("File not found on path: " + filePath);
        } catch (IOException ioException) {
            throw new IOException("Error happened while reading file");
        }

        data = new OsmData(
                mbr,
                nodeMap,
                wayMap,
                relationMap
        );
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
            existing.setMinZoomLevel(calculateZoomLevel(tags));
            return;
        }
        Relation relation = new Relation(relationID, tags, members);
        relation.setMinZoomLevel(calculateZoomLevel(tags));
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
                Node node = nodeMap.get(ndID);
                if (node != null) {
                    nodes.add(node);
                }
            }
            if (line.contains("<tag")) {
                String k = getAttribute(line, "k");
                String v = getAttribute(line, "v");
                tags.put(k, v);
            }
        }

        Way way = new Way(wayID, tags, nodes);
        way.setMinZoomLevel(calculateZoomLevel(tags));
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

    @Override
    public String getAttribute(String str, String key) {
        String subStr = super.getAttribute(str,key);
        return subStr
                .replace("&amp;", "&")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"");
    }

    private double calculateZoomLevel(HashMap<String, String> tags) {
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
                case "river", "canal" -> 14.0;
                case "stream"         -> 12.0;
                default               -> 13.0;
            };
        }

        String landuse = tags.get("landuse");
        if (landuse != null) {
            return switch (landuse) {
                case "forest" -> 0.0;
                case "grass" -> 15.0;
                case "farmland", "farmyard" -> 13.0;
                case "residential", "commercial",
                     "retail"                    -> 10.0;
                case "industrial"                -> 10.0;
                default                          -> 0.0;
            };
        }

        if (tags.containsKey("natural")) return 0.0;
        if (tags.containsKey("aeroway")) return 10.0;
        if (tags.containsKey("amenity") || tags.containsKey("leisure")) return 11.0;
        if (tags.containsKey("man_made")) return 13.0;
        if (tags.containsKey("tourism") || tags.containsKey("historic")) return 13.0;
        if (tags.containsKey("barrier")) return 14.0;
        return 0.0;
    }
}
