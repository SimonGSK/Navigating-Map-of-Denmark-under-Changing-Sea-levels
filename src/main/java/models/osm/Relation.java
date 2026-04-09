package models.osm;

import models.geometry.BoundingBox;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Relation extends Element {
    private List<Member> members;

    public Relation(long id, HashMap<String, String> tags, List<Member> members) {
        super(id, tags, computeMbr(members));
        this.members = members;
    }

    static private BoundingBox computeMbr(List<Member> members) {
        return BoundingBox.computeMbr(members.stream().map(Member::getElement).toList());
    }

    public boolean addMember(Member member) {
        if (members == null) {
            members = new ArrayList<>();
        }
        setMbr(updateMbr(member));
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
        setMbr(updateMbr(this.members));
    }

    private BoundingBox updateMbr(List<Member> members) {
        return BoundingBox.computeMbr(members.stream().map(Member::getElement).toList());
    }

    private BoundingBox updateMbr(Member newMember) {
        return getMbr().getExpanded(newMember.getElement().getMbr());
    }

    @Override
    public void draws(Graphics2D gc) {
        // TODO: This should be cleaned up. If the method doesn't do anything, we should look at how we can restructure the purpose of Drawable.
    }
}
