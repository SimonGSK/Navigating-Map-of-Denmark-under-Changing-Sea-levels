package models.osm;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Relation extends Element {
    private List<Member> members;
    private HashMap<String, String> tags;

    public Relation(long id,List<Member> members, HashMap<String, String> tags) {
        super(id);
        this.members = members;
        this.tags = tags;
    }


    public boolean addMember(Member member) {
        if (members == null) {
            members = new ArrayList<>();
        }

        return members.add(member);
    }


    public List<Member> getMembers() {
        if (members == null) {
            return null;
        }

        return new ArrayList<>(members);
    }

    public void setMembers(List<Member> members){
        this.members = members;
    }

    public void setTags (HashMap<String, String> tags){
        this.tags = tags;
    }

    @Override
    public void drawForTest(Graphics2D gc, Color color, Integer strokeWidth){

    }
}
