package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Parser implements IParser{

    private final String fileName;
    private final List<Double> boundingBox = new ArrayList<>();
    private final HashMap<Long, Node> osmNodeMap = new HashMap<>();
    private final HashMap<Long, Way> osmWayMap = new HashMap<>();
    private final HashMap<Long, Relation> osmRelationMap = new HashMap<>();

    public Parser(String filename) {
        this.fileName = filename;
    }

    @Override
    public void parse(){
        try {
            InputStream is = Parser.class.getResourceAsStream("/data/" + fileName);
            BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            String line;

            while ((line = br.readLine()) != null){
                if(line.contains("<bounds")) {
                    double minlat = getAttributeDouble(line, "minlat");
                    double minlon = getAttributeDouble(line, "minlon");
                    double maxlat = getAttributeDouble(line, "maxlat");
                    double maxlon = getAttributeDouble(line, "maxlon");
                    boundingBox.add(minlat);
                    boundingBox.add(minlon);
                    boundingBox.add(maxlat);
                    boundingBox.add(maxlon);

                } else if (line.contains("<node") && !line.contains("</node")) {
                    double lat = getAttributeDouble(line, "lat");
                    double lon = getAttributeDouble(line, "lon");
                    long id = getAttributeLong(line, "id");
                    Node node = new Node (id, lat, lon);
                    osmNodeMap.put(id, node);

                } else if (line.contains("<way")) {
                    List<Node> nodes = new ArrayList<>();
                    HashMap<String, String> tags = new HashMap<>();

                    long wayID = getAttributeLong(line, "id");

                    while(!line.contains("</way>")){
                        line = br.readLine().trim();
                        if(line.contains("<nd")){
                            long ndID = getAttributeLong(line, "ref");
                            nodes.add(getOsmNodeMap().get(ndID));
                        }
                        if(line.contains("<tag")){
                            String k = getAttribute(line, "k");
                            String v = getAttribute(line, "v");
                            tags.put(k, v);
                        }
                    }
                    Way way = new Way(wayID, nodes, tags);
                    osmWayMap.put(wayID, way);

                } else if (line.contains("<relation ") || line.contains("<relation>")) {
                    List<Member> members = new ArrayList<>();
                    HashMap<String, String> tags = new HashMap<>();

                    long relationID = getAttributeLong(line, "id");

                    while (!line.contains("</relation>")) {
                        line = br.readLine().trim();

                        if (line.contains("<member")) {
                            String type = getAttribute(line, "type");
                            Long ref = getAttributeLong(line, "ref");
                            String role = getAttribute(line, "role");

                            if (type.equals("node")) {
                                if(osmNodeMap.containsKey(ref)){
                                    Member member = new Member(osmNodeMap.get(ref), role);
                                    members.add(member);
                                }
                            } else if (type.equals("way")) {
                                if(osmWayMap.containsKey(ref)){
                                    Member member = new Member(osmWayMap.get(ref), role);
                                    members.add(member);
                                }
                            } else if (type.equals("relation")) {

                                if (!osmRelationMap.containsKey(ref)) {
                                    List<Member> newMembers = new ArrayList<>();
                                    HashMap<String, String> newTags = new HashMap<>();
                                    Relation newRelation = new Relation(ref, newMembers, newTags);
                                    osmRelationMap.put(ref, newRelation);
                                }
                                Member member = new Member(osmRelationMap.get(ref), role);
                                members.add(member);
                            }
                        } else if (line.contains("<tag")) {
                            String k = getAttribute(line, "k");
                            String v = getAttribute(line, "v");
                            tags.put(k, v);
                        }
                    }
                    if (osmRelationMap.containsKey(relationID)){
                        Relation existing = osmRelationMap.get(relationID);
                        existing.setMembers(members);
                        existing.setTags(tags);
                    } else {
                        Relation relation = new Relation(relationID, members, tags);
                        osmRelationMap.put(relationID, relation);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public String getAttribute(String s, String key) {
        String pattern = key + "=\"";
        int start = s.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int valueStart = start + pattern.length();
        int valueEnd = s.indexOf('"', valueStart);
        return s.substring(valueStart, valueEnd);
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
    public List<Double> getBoundingBox() {
        return boundingBox;
    }

    @Override
    public HashMap<Long, Node> getOsmNodeMap() {
        return osmNodeMap;
    }

    @Override
    public HashMap<Long, Way> getOsmWayMap() {
        return osmWayMap;
    }

    @Override
    public HashMap<Long, Relation> getOsmRelationMap() {
        return osmRelationMap;
    }
}
