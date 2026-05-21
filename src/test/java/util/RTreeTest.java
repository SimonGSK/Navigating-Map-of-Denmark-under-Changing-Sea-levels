package util;

import enums.ElementType;
import models.RTree.*;
import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class RTreeTest {
    private static final BoundingBox WORLD = new BoundingBox(-90,-180, 90, 180);

    private static final BoundingBox BORNHOLM = new BoundingBox(54.9, 14.6, 55.3, 15.2);

    private static final BoundingBox PACIFIC = new BoundingBox(10.0, -160.0, 11.0, -159.0);

    private static Node node(long id, double lat,double lon) {
        return new Node(id, lat, lon);
    }
    private static Way closedWay(long id, double minLat, double minLon, double maxLat, double maxLon) {
        Node a = node(id * 100 + 1, minLat, minLon);
        Node b = node(id * 100 + 2, maxLat, maxLon);
        Node c = node(id * 100 + 3, minLat, minLon);
        Node d = node(id * 100 + 4, maxLat, maxLon);
        Node close = node(id * 100 + 1, minLat, minLon);
        return new Way(id, new HashMap<>(), List.of(a, b, c, d, close));
    }
    private static Way openWay(long id, double minLat, double minLon, double maxLat, double maxLon) {
        Node a = node(id * 100 + 1, minLat, minLon);
        Node b = node(id * 100 + 2, maxLat, maxLon);
        return new  Way(id, new HashMap<>(), List.of(a, b));
    }
    private static Relation relationAroundWay(long id, Way way) {
        models.osm.Member m = new models.osm.Member(way, ElementType.way, "outer");
        return new Relation(id, new HashMap<>(), List.of(m));
    }
    private static Tree emptyTree() {
        return new Tree(WORLD, new HashMap<>(), new HashMap<>(),  new HashMap<>());
    }
    private static Tree treeWith(List<?> elements) {
        Map <Long, Node> nodes = new LinkedHashMap<>();
        Map <Long, Way> ways = new LinkedHashMap<>();
        Map <Long, Relation> relations = new LinkedHashMap<>();

        for (Object o : elements) {
            switch (o) {
                case Node n -> nodes.put(n.getId(), n);
                case Way w -> ways.put(w.getId(), w);
                case Relation r -> relations.put(r.getId(), r);
                default -> throw new IllegalArgumentException("Unknown type: " + o.getClass());
            }
        }
        return new Tree(WORLD, nodes, ways, relations);
    }
    @Nested
    @DisplayName("Constructor validation")
    class ConstructorTests{
        @Test
        @DisplayName("Null MBR throws RuntimeException")
        void constructor_nullMbr_throws() {
            assertThrows(RuntimeException.class, () -> new Tree(null, new HashMap<>(), new HashMap<>(), new HashMap<>()));
        }
        @Test
        @DisplayName("Null nodeMap throws RuntimeException")
        void constructor_nullNodeMap_throws() {
            assertThrows(RuntimeException.class, () -> new Tree(WORLD, null, new HashMap<>(), new HashMap<>()));
        }
        @Test
        @DisplayName("Null relationMap throws RuntimeException")
        void constructor_nullRelationMap_throws() {
            assertThrows(RuntimeException.class, () -> new Tree(WORLD, new HashMap<>(), new HashMap<>(), null));
        }
        @Test
        @DisplayName("Empty maps produce a valid, non-null tree")
        void constructor_emptyMaps_createsTree() {
            assertDoesNotThrow(() -> emptyTree());
        }
    }
    @Nested
    @DisplayName("Insert")
    class InsertTests {
        @Test
        @DisplayName("Inserted node is found by search")
        void insert_singleNode_foundBySearch() {
            Tree tree = emptyTree();
            Node n = node(1, 55.1, 14.9);
            tree.insert(n);
            assertTrue(tree.search(BORNHOLM).nodeList().contains(n));
        }
        @Test
        @DisplayName("Inserted way is found by search")
        void insert_singleWay_foundBySearch() {
            Tree tree = emptyTree();
            Way w = openWay(1, 55.0, 14.7, 55.2, 15.0);
            tree.insert(w);
            assertTrue(tree.search(BORNHOLM).wayList().contains(w));
        }
        @Test
        @DisplayName("Inserted relation is found by search")
        void insert_singleRelation_foundBySearch() {
            Tree tree = emptyTree();
            Relation r = relationAroundWay(99, openWay(1, 55.0, 14.7, 55.2, 15.0));
            tree.insert(r);
            assertTrue(tree.search(BORNHOLM).relationList().contains(r));
        }
        @Test
        @DisplayName("Node outside search area is not returned")
        void insert_nodeOutsideSearchArea_notFound() {
            Tree tree = emptyTree();
            Node n = node(1, 10.5, -159.5); //Pacific
            tree.insert(n);
            assertFalse(tree.search(BORNHOLM).nodeList().contains(n));
        }
        @Test
        @DisplayName("MBR is non-null after first insert")
        void insert_firstElement_mbrNonNull() {
            Tree tree = emptyTree();
            tree.insert(node(1, 55.1, 14.9));

            assertNotNull(tree.getMbr());
        }
        /**
         * 50 inserts (> max = 30) forces at least one node split.
         * Every element must still be retrievable afterwards.
         */
        @Test
        @DisplayName("Inserting >max elements triggers split; all elements still searchable")
        void insert_exceedsMaxCapacity_allElementsSearchable() {
            Tree tree = emptyTree();
            List<Node> nodes = new ArrayList<>();

            for (int i = 0; i < 50; i++) {
                double lat = 55.00 + i * 0.003;
                double lon = 14.70 + i * 0.003;
                Node n = node(i, lat, lon);
                nodes.add(n);
                tree.insert(n);
            }
            List<Node> found = tree.search(BORNHOLM).nodeList();
            for (Node n : nodes) {
                assertTrue(found.contains(n), "Node id=" + n.getId() + "should survive node splits");
            }
        }
    }
    // Search
    @Nested
    @DisplayName("Search")
    class SearchTests {
        @Test
        @DisplayName("Search on empty tree returns empty resuslts")
        void search_emptyTree_emptyResults() {
            SearchResults r = emptyTree().search(BORNHOLM);

            assertTrue(r.nodeList().isEmpty());
            assertTrue(r.wayList().isEmpty());
            assertTrue(r.relationList().isEmpty());
        }
        @Test
        @DisplayName("Only elements overlapping the search box are returned")
        void search_mixedPositions_onlyOverlappingReturned() {
            Node inside = node(1, 55.1, 14.9); //Bornholm
            Node outside = node(2, 10.5, -159.5); // Pacific

            Tree tree = treeWith(List.of(inside, outside));
            List<Node> found = tree.search(BORNHOLM).nodeList();

            assertTrue(found.contains(inside));
            assertFalse(found.contains(outside));
        }
        @Test
        @DisplayName("Search returns all three element types simultaneously")
        void search_allThreeTypes_allReturned() {
            Node n = node(1, 55.1, 14.9);
            Way w = openWay(2, 55.0,  14.7, 55.2, 15.0);
            Relation r = relationAroundWay(3, openWay(4, 55.0, 14.7, 55.1, 14.9));

            Tree tree = treeWith(List.of(n, w, r));
            SearchResults sr =  tree.search(BORNHOLM);

            assertFalse(sr.nodeList().isEmpty(), "nodes expected");
            assertFalse(sr.wayList().isEmpty(), "ways expected");
            assertFalse(sr.relationList().isEmpty(), "relations expected");
        }
        @Test
        @DisplayName("Node exactly on search-box boundary is found")
        void search_nodeOnBoundary_isFound() {
            Node n = node(1, 54.9, 14.6);

            assertTrue(treeWith(List.of(n)).search(BORNHOLM).nodeList().contains(n));
        }
        @Test
        @DisplayName("Point bounding box (zero area) finds overlapping way")
        void search_pointBox_findsContainingWay() {
            Way w = openWay(1, 55.0, 14.7, 55.2, 15.0);
            Tree tree = treeWith(List.of(w));

            BoundingBox point = new BoundingBox(55.1, 14.9, 55.1, 14.9);

            assertTrue(tree.search(point).wayList().contains(w));
        }

        @Test
        @DisplayName("World-sized search box returns every inserted element")
        void search_worldBox_returnsEverything() {
            Node n1 = node(1,  55.1,  14.9);
            Node n2 = node(2, -33.8, 151.2); // Sydney
            Node n3 = node(3,  40.7, -74.0); // New York

            List<Node> found = treeWith(List.of(n1, n2, n3)).search(WORLD).nodeList();

            assertTrue(found.containsAll(List.of(n1, n2, n3)));
        }

        @Test
        @DisplayName("Exact element count is returned when all elements overlap search area")
        void search_allElementsInArea_exactCountReturned() {
            int count = 40;
            List<Node> nodes = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                nodes.add(node(i, 55.00 + (i % 10) * 0.02, 14.70 + (i / 10) * 0.04));
            }
            assertEquals(count, treeWith(nodes).search(BORNHOLM).nodeList().size());
        }

        @Test
        @DisplayName("Ways are sorted by area descending")
        void search_multipleWays_sortedLargestFirst() {
            Way small = closedWay(1, 55.10, 14.80, 55.11, 14.81);
            Way large = closedWay(2, 55.00, 14.70, 55.20, 15.00);

            List<Way> ways = treeWith(List.of(small, large)).search(BORNHOLM).wayList();

            assertEquals(2, ways.size());
            assertTrue(ways.get(0).getArea() >= ways.get(1).getArea(),
                    "Largest way must come first");
        }

        @Test
        @DisplayName("Relations are sorted by area descending")
        void search_multipleRelations_sortedLargestFirst() {
            Relation small = relationAroundWay(1, closedWay(10, 55.10, 14.80, 55.11, 14.81));
            Relation large = relationAroundWay(2, closedWay(20, 55.00, 14.70, 55.20, 15.00));

            List<Relation> rels = treeWith(List.of(small, large)).search(BORNHOLM).relationList();

            assertEquals(2, rels.size());
            assertTrue(rels.get(0).getArea() >= rels.get(1).getArea(),
                    "Largest relation must come first");
        }
    }


    //  SearchResults (unit-tested independently of Tree)


    @Nested
    @DisplayName("SearchResults")
    class SearchResultsTests {

        @Test
        @DisplayName("isEmpty() – SearchResults starts with empty ArrayLists")
        void searchResults_defaultconstructor_allListsEmpty() {
            SearchResults sr = new SearchResults();
            assertTrue(sr.nodeList().isEmpty(), "nodeList is not empty");
            assertTrue(sr.wayList().isEmpty(), "wayList is not empty");
            assertTrue(sr.relationList().isEmpty(), "relationList is not empty");
        }

        @Test
        @DisplayName("not null – SearchResults starts with empty ArrayLists")
        void searchResults_defaultconstructor_listsNotNull() {
            SearchResults sr = new SearchResults();
            assertNotNull(sr.nodeList(), "nodeList is null");
            assertNotNull(sr.wayList(), "wayList is null");
            assertNotNull(sr.relationList(), "relationList is null");
        }

        @Test
        @DisplayName("add(node) — appears only in nodeList")
        void searchResults_addNode_appearsOnlyInNodeList() {
            SearchResults sr = new SearchResults();
            sr.add(ElementType.node, node(1, 55.1, 14.9));

            assertEquals(1, sr.nodeList().size());
            assertTrue(sr.wayList().isEmpty());
            assertTrue(sr.relationList().isEmpty());
        }

        @Test
        @DisplayName("add(way) — appears only in wayList")
        void searchResults_addWay_appearsOnlyInWayList() {
            SearchResults sr = new SearchResults();
            sr.add(ElementType.way, openWay(1, 55.0, 14.7, 55.2, 15.0));

            assertEquals(1, sr.wayList().size());
            assertTrue(sr.nodeList().isEmpty());
            assertTrue(sr.relationList().isEmpty());
        }

        @Test
        void searchResults_sort_waysLargestFirst() {
            SearchResults sr = new SearchResults();
            Way small = closedWay(1, 55.10, 14.80, 55.11, 14.81);
            Way large = closedWay(2, 55.00, 14.70, 55.20, 15.00);

            sr.add(ElementType.way, small);
            sr.add(ElementType.way, large);

            System.out.println("small getArea: " + small.getArea());
            System.out.println("large getArea: " + large.getArea());
            System.out.println("small MBR area: " + small.getMbr().area());
            System.out.println("large MBR area: " + large.getMbr().area());
            sr.sort();


            assertTrue(sr.wayList().get(0).getMbr().area() >= sr.wayList().get(1).getMbr().area());
            assertEquals(large.getId(), sr.wayList().get(0).getId(), "large way should be first");
            assertEquals(small.getId(), sr.wayList().get(1).getId(), "small way should be second");
        }
        @Test
        @DisplayName("sort() — relations ordered largest-first")
        void searchResults_sort_relationsLargestFirst() {
            SearchResults sr    = new SearchResults();
            Relation      small = relationAroundWay(1, closedWay(10, 55.10, 14.80, 55.11, 14.81));
            Relation      large = relationAroundWay(2, closedWay(20, 55.00, 14.70, 55.20, 15.00));

            sr.add(ElementType.relation, small);
            sr.add(ElementType.relation, large);
            sr.sort();

            assertEquals(large, sr.relationList().get(0));
            assertEquals(small, sr.relationList().get(1));
        }

        @Test
        @DisplayName("clear() – all elements are removed")
        void searchResults_clear() {
            SearchResults sr = new SearchResults();

            int n = 3000;
            for (int i = 0; i < n; i++) {
                sr.add(ElementType.node, new Node((long) (Math.random() * 1000), 0,0));
            }
            assertTrue(sr.nodeList().size() == n, "nodeList().size() didn't match number of nodes inserted");
            assertFalse(sr.nodeList().isEmpty(), "nodeList is empty");

            sr.clear();
            assertTrue(sr.nodeList().isEmpty(), "nodeList is not empty");
        }

        @Test
        @DisplayName("clear() – backing-array capacity equals pre-clear size")
        void searchResults_clear_preservesCapacity() throws Exception {
            SearchResults sr = new SearchResults();

            int n = 3000;
            for (int i = 0; i < n; i++) {
                sr.add(ElementType.node, new Node((long) (Math.random() * 1000), 0, 0));
            }

            sr.clear();

            // elements are gone
            assertEquals(0, sr.nodeList().size(),
                "nodeList size should be 0 after clear()");

            // backing array was trimmed to n before clear(), so capacity must still be n
            assertEquals(n, getCapacity(sr.nodeList()),
                "nodeList backing-array capacity should equal the pre-clear element count");

            // lists that were never populated should stay at capacity 0
            assertEquals(0, getCapacity(sr.wayList()),
                "wayList backing-array capacity should be 0 (never populated)");
            assertEquals(0, getCapacity(sr.relationList()),
                "relationList backing-array capacity should be 0 (never populated)");
        }

        private static int getCapacity(ArrayList<?> list) throws Exception {
            Field f = ArrayList.class.getDeclaredField("elementData");
            f.setAccessible(true);
            return ((Object[]) f.get(list)).length;
        }

    }


    //  TreeNode (unit-tested independently of Tree)


    @Nested
    @DisplayName("TreeNode")
    class TreeNodeTests {

        @Test
        @DisplayName("new TreeNode(true) — isLeaf returns true")
        void treeNode_leafFlag_isLeafTrue() {
            assertTrue(new TreeNode(true).isLeaf());
        }

        @Test
        @DisplayName("new TreeNode(false) — isLeaf returns false")
        void treeNode_internalFlag_isLeafFalse() {
            assertFalse(new TreeNode(false).isLeaf());
        }

        @Test
        @DisplayName("isOverflowing is false when entries == max")
        void treeNode_atMax_notOverflowing() {
            TreeNode tn = new TreeNode(true);
            int max = 30;
            for (int i = 0; i < max; i++) {
                tn.entries.add(new LeafEntry(node(i, 55.0 + i * 0.001, 14.7)));
            }
            assertFalse(tn.isOverflowing(max));
        }

        @Test
        @DisplayName("isOverflowing is true when entries == max + 1")
        void treeNode_aboveMax_isOverflowing() {
            TreeNode tn = new TreeNode(true);
            int max = 30;
            for (int i = 0; i <= max; i++) {  // max+1 entries
                tn.entries.add(new LeafEntry(node(i, 55.0 + i * 0.001, 14.7)));
            }
            assertTrue(tn.isOverflowing(max));
        }
    }


    // 6. TreeData (unit-tested independently of Tree)


    @Nested
    @DisplayName("TreeData")
    class TreeDataTests {

        @Test
        @DisplayName("Default constructor — iterator produces no elements")
        void treeData_defaultConstructor_isEmpty() {
            assertFalse(new TreeData().iterator().hasNext());
        }

        @Test
        @DisplayName("Null maps throw RuntimeException")
        void treeData_nullMaps_throws() {
            assertThrows(RuntimeException.class,
                    () -> new TreeData(null, new HashMap<>(), new HashMap<>()));
        }

        @Test
        @DisplayName("Node map contents appear in iteration")
        void treeData_withNodes_iteratesNodes() {
            Node n = node(1, 55.1, 14.9);
            TreeData data = new TreeData(Map.of(1L, n), new HashMap<>(), new HashMap<>());

            long count = 0;
            for (var ignored : data) count++;
            assertEquals(1, count);
        }

        @Test
        @DisplayName("Standalone way (not in any relation) appears in iteration")
        void treeData_standaloneWay_appearsInIteration() {
            Way w = openWay(1, 55.0, 14.7, 55.2, 15.0);
            TreeData data = new TreeData(new HashMap<>(), Map.of(1L, w), new HashMap<>());

            List<models.osm.Element> elements = new ArrayList<>();
            data.forEach(elements::add);

            assertTrue(elements.contains(w));
        }
    }


    // 7. Edge cases & stress


    @Nested
    @DisplayName("Edge cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Two nodes at identical coordinates are both found")
        void insert_twoNodesAtSameCoord_bothSearchable() {
            Tree tree = emptyTree();
            Node n1 = node(1, 55.1, 14.9);
            Node n2 = node(2, 55.1, 14.9); // same position, different id

            tree.insert(n1);
            tree.insert(n2);

            List<Node> found = tree.search(BORNHOLM).nodeList();
            assertTrue(found.contains(n1));
            assertTrue(found.contains(n2));
        }

        @Test
        @DisplayName("Interleaved node/way inserts — every element is searchable")
        void insert_interleavedTypes_allFound() {
            Tree tree = emptyTree();
            List<models.osm.Element> inserted = new ArrayList<>();

            for (int i = 0; i < 20; i++) {
                double lat = 55.00 + i * 0.01;
                double lon = 14.70 + i * 0.01;
                models.osm.Element e = (i % 2 == 0)
                        ? node(i, lat, lon)
                        : openWay(i, lat, lon, lat + 0.005, lon + 0.005);
                inserted.add(e);
                tree.insert(e);
            }

            SearchResults sr = tree.search(BORNHOLM);
            List<models.osm.Element> allFound = new ArrayList<>();
            allFound.addAll(sr.nodeList());
            allFound.addAll(sr.wayList());

            for (models.osm.Element e : inserted) {
                assertTrue(allFound.contains(e), "Element should survive interleaved inserts");
            }
        }

        @Test
        @DisplayName("Elements in different hemispheres are each found only in the right box")
        void search_globalElements_eachFoundInCorrectRegion() {
            Node bornholm = node(1,  55.1,  14.9);
            Node pacific  = node(2,  10.5, -159.5);

            Tree tree = treeWith(List.of(bornholm, pacific));

            assertTrue( tree.search(BORNHOLM).nodeList().contains(bornholm));
            assertFalse(tree.search(BORNHOLM).nodeList().contains(pacific));

            assertTrue( tree.search(PACIFIC).nodeList().contains(pacific));
            assertFalse(tree.search(PACIFIC).nodeList().contains(bornholm));
        }

        /**
         * Stress test: 200 nodes within Bornholm — none lost after repeated splits.
         */
        @Test
        @DisplayName("Stress: 200 nodes — none lost after multiple splits")
        void stress_200Nodes_allFound() {
            Tree tree = emptyTree();
            List<Node> nodes = new ArrayList<>();

            for (int i = 0; i < 200; i++) {
                double lat = 55.00 + (i % 20) * 0.015;
                double lon = 14.70 + (i / 20) * 0.025;
                Node n = node(i, lat, lon);
                nodes.add(n);
                tree.insert(n);
            }

            assertEquals(200, tree.search(BORNHOLM).nodeList().size(),
                    "All 200 nodes must survive repeated splits");
        }
    }
}
