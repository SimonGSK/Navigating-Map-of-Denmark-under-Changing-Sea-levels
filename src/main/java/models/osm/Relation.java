package models.osm;

import enums.ElementType;
import models.geometry.BoundingBox;

import java.awt.*;
import java.io.Serializable;
import java.util.*;
import java.util.List;


public class Relation extends Element implements Iterable<Member>, Serializable {
    private List<Member> members;

    public Relation(long id, HashMap<String, String> tags, List<Member> members) {
        super(id, ElementType.relation, tags, computeMbr(members));
        this.members = members;
    }

    static private BoundingBox computeMbr(List<Member> members) {
        return BoundingBox.computeMbr(members.stream().map(Member::getElement).toList());
    }

    public boolean isEmpty() {
        return members.isEmpty();
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

    public void updateMbr() {
        setMbr(updateMbr(members));
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

    @Override
    public Iterator<Member> iterator() {
        if (members == null) {
            return Collections.emptyIterator();
        }
        return members.iterator();
    }
}
