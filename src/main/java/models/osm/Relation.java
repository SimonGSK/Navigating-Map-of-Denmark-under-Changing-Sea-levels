package models.osm;

import models.geometry.BoundingBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Relation extends Element {
    private List<Member> members;

    public Relation(long id, HashMap<String, String> tags, BoundingBox mbr, List<Member> members) {
        super(id, tags, mbr);
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

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    @Override
    public void draws(Graphics2D gc) {
        // TODO: This should be cleaned up. If the method doesn't do anything, we should look at how we can restructure the purpose of Drawable.
    }
}
