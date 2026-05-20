package util;

import models.geometry.AdaptivePath;
import models.geometry.Coordinate;
import models.osm.Member;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import enums.ElementType;
import org.junit.jupiter.api.Test;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RelationTest {

    @Test
    void setAndGetRingShapes_returnsSameList() {
        Relation relation = new Relation(1L, new HashMap<>(), new ArrayList<>());

        List<AdaptivePath> rings = new ArrayList<>();
        rings.add(new AdaptivePath(
                List.of(
                        new double[]{0.0, 0.0},
                        new double[]{1.0, 0.0},
                        new double[]{1.0, 1.0},
                        new double[]{0.0, 1.0}
                ),
                true
        ));

        relation.setRingShapes(rings);

        assertNotNull(relation.getRingShapes());
        assertEquals(1, relation.getRingShapes().size());
        assertSame(rings, relation.getRingShapes());
    }

    @Test
    void getNodes_collectsNodesFromWayMembers() {
        Node n1 = new Node(1L, 55.0, 12.0);
        Node n2 = new Node(2L, 55.1, 12.1);
        Way way = new Way(10L, new HashMap<>(), List.of(n1, n2));

        Member member = new Member(way, ElementType.way, "outer");
        Relation relation = new Relation(1L, new HashMap<>(), new ArrayList<>(List.of(member)));

        assertEquals(2, relation.getNodes().size());
        assertTrue(relation.getNodes().contains(n1));
        assertTrue(relation.getNodes().contains(n2));
    }

    @Test
    void emptyRelation_hasNoMembersButDoesNotCrash() {
        Relation relation = new Relation(1L, new HashMap<>(), new ArrayList<>());

        assertTrue(relation.isEmpty());
        assertNotNull(relation.iterator());
    }
}
