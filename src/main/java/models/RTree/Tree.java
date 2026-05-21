package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.*;
import models.utils.UtilityTools;

import java.io.Serializable;
import java.util.*;

public class Tree implements Serializable {
    static private final int min = 1; // Must be >= 1
    static private int max = 30;
    private TreeNode root;
    private BoundingBox mbr;
    private final TreeData treeData;
    private transient SearchResults searchResults;
    private double zoomLevel;

    /**
     * Updates the current zoomLevel of the application, which influences how many elements are filtered out during search queries.
     * Lower values means more element are filtered out, and higher values means that less elements are filtered out.
     * @param zoomLevel
     */
    public void setZoomLevel(double zoomLevel) {
        this.zoomLevel = zoomLevel;
    }

    /**
     * A spatial indexing R-Tree implementation used for efficiently querying OSM data in a 2-dimensional space
     * @see BoundingBox
     * @see Element
     * @param max The maximum number of elements allowed in each node of the R-Tree
     * @param mbr The minimum-bounding-rectanlges
     * @param nodeMap Nodes from OSM data, indexed in a map according to ids
     * @param wayMap Ways from OSM data, indexed in a map according to ids
     * @param relationMap Relations from OSM data, indexed in a map according to ids
     */
    public Tree(int max, BoundingBox mbr, Map<Long,Node> nodeMap, Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        if (max < (min * 2)) {
            throw new RuntimeException("max must be greater than min * 2");
        }
        Tree.max = max;

        if (mbr == null || nodeMap == null || wayMap == null || relationMap == null) {
            throw new RuntimeException("nodeWay, wayMap, and relationMap must not be null");
        }
        this.mbr = mbr;
        this.treeData = new TreeData(nodeMap, wayMap, relationMap);
        this.treeData.forEach(this::insert);
    }

    /**
     * A spatial indexing R-Tree implementation used for efficiently querying OSM data in a 2-dimensional space.
     * Each tree-node can contain up to 30 elements.
     * @param mbr The minimum-bounding-rectanlges
     * @param nodeMap Nodes from OSM data, indexed in a map according to ids
     * @param wayMap Ways from OSM data, indexed in a map according to ids
     * @param relationMap Relations from OSM data, indexed in a map according to ids
     */
    public Tree(BoundingBox mbr, Map<Long,Node> nodeMap, Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        this(Tree.max, mbr,nodeMap,wayMap,relationMap);
    }

    /**
     * Helper function to update the minimum-bounding-rectangle (MBR) of a tree-node after its containing entries have been changed
     * @param node The tree-node whose MBR you want to update
     */
    public void updateTreeNodeMbr(TreeNode node) {
        if (node == null || node.entries == null || node.entries.isEmpty()) {
            return;
        }
        node.setMbr(computeMBR(node.entries));
    }

    /**
     * A helper function for testing purposes.
     * @return Minimum-bounding-rectangle of the entire Tree
     */
    public BoundingBox getMbr() {
        return mbr;
    }

    /**
     * Performs a 2-dimenstional range search query based on minimum and maximum values of latitude and longitude.
     * In other words, a spatial search query that finds all elements in the Tree that are located within a BoundingBox.
     * @param searchArea The BoundingBox
     * @return An instance of @code{SearchResults} containing all nodes, ways, and relations, whose MBRs overlaps with the search query BoundingBox
     */
    public SearchResults search(BoundingBox searchArea) {
        if (searchResults == null) {
            searchResults = new SearchResults();
        }

        searchResults.clear();
        if (root != null) {
            searchRecursive(root, searchArea, searchResults);
        }
        searchResults.sort();
        return searchResults;
    }

    /**
     * Recursively finds all entries from a TreeNode whose minimum-bounding-rectangles overlap with the searchArea and adds them to searchResults.
     * Helper function; invoked by Tree.search()
     * @param node The TreeNode whose entries to examine for the search query
     * @param searchArea The BoundingBox used for the search query
     * @param searchResults The instance of SearchResults which will be returned in Tree.search()
     */
    private void searchRecursive(TreeNode node, BoundingBox searchArea, SearchResults searchResults) {
        for (TreeEntry entry : node.entries) {
            if (entry.overlaps(searchArea)) {
                switch (entry) {
                    case LeafEntry leaf -> {
                        if (!leaf.element().isVisibleOnZoom(zoomLevel)) {
                            continue;
                        }
                        searchResults.add(leaf.element().getType(), leaf.element());
                    }
                    case NodeEntry nonLeaf -> {
                        if (!nonLeaf.child().isVisibleOnZoom(zoomLevel)) {
                            continue;
                        }
                        searchRecursive(nonLeaf.child(), searchArea, searchResults);
                    }
                }
            }
        }
    }

    /**
     * Used by GraphicsRenderer.java to renderer BoundingBoxes for each TreeNode in the Tree.
     * @return A list of the minimum-bounding-rectangles for each TreeNode in the Tree.
     */
    public List<BoundingBox> getMBRList() {
        List<BoundingBox> mbrList = new ArrayList<>();
        if (root != null) {
            root._getMBR(mbrList);
        }
        return mbrList;
    }

    /**
     * Finds the node nearest to the relative coordinate of the user's cursor.
     * Only considers nodes that are part of a Way element with the tag "highway".
     *
     * Uses Tree.ChooseLeaf() to first find the optimal TreeNode for insertion of a new element into the Tree.
     * This returns a leaf TreeNode. All entries in the leaf are processed to filter all nodes that are part of a "highway" Way-element.
     * If no nodes are found, we run a standard search query of the parent of the leaf to broaden our search, and again filter all nodes.
     * If no nodes are found, there are no candidates for nearest-neighbor search – returns null – else, the distance from cursor to node is calculated and used as radius to compute the outer square for a new search query.
     * Again, all nodes are filtered to only consider nodes from "highway" Way-elements, and the node with the shortest distance to cursor is returned.
     *
     * @see models.ui.AppController.getCursorCoordinate()
     * @param cursor The coordinate of the cursor (cursor's relative position in longitude and latitude)
     * @return The node with the shortest distance to cursor, that's part of a Way-element with the "highway"-tag
     */
    public Node getNearestNode(Coordinate cursor) {
        if (root == null) {
            return null;
        }

        List<TreeNode> path = new ArrayList<>();
        TreeNode nearestTreeNode = chooseLeaf(root,new BoundingBox(cursor.getLat(), cursor.getLon(), cursor.getLat(),cursor.getLon()),path);

        Set<Node> nodes = new HashSet<>();
        for (TreeEntry entry : nearestTreeNode.entries) {
            if (entry instanceof LeafEntry(Element element)) {
                if (element instanceof Way way && way.getTag("highway") != null) {
                    nodes.addAll(way.getNodes());
                }
            }
        }

        // If the leaf had no highway ways, walk up the path until we find some
        if (nodes.isEmpty()) {
            for (int i = path.size() - 2; i >= 0 && nodes.isEmpty(); i--) {
                TreeNode ancestor = path.get(i);
                SearchResults ancestorResults = search(ancestor.getMbr());
                for (Way w : ancestorResults.wayList()) {
                    if (w.getTag("highway") != null) {
                        nodes.addAll(w.getNodes());
                    }
                }
            }
        }

        if (nodes.isEmpty()) {
            System.out.println("***** No highway ways found in search area *****");
            return null;
        }

        nearestNodeDist nearestNode = _findNearestNodeDist(cursor, new ArrayList<>(nodes));
        double radius = nearestNode.dist();

        // Outer square
        BoundingBox searchArea = new BoundingBox(
                cursor.getLat() - radius, cursor.getLon() - radius,
                cursor.getLat() + radius, cursor.getLon() + radius
        );

        SearchResults searchResults = search(searchArea);

        nodes.clear();
        for (Way w : searchResults.wayList()) {
            if (w.getTag("highway") != null) {
                nodes.addAll(w.getNodes());
            }
        }

        nearestNodeDist result = _findNearestNodeDist(cursor, new ArrayList<>(nodes));
        return result.node();
    }

    /**
     * Helper function for Tree.getNearestNode(). Used to find the Node with the shortest euclidean distance to a given coordinate, from a list of Nodes.
     * @see nearestNodeDist
     * @param coordinate The Coordinate to find the nearest node to
     * @param nodes The list of nodes to examine
     * @return A nearestNodeDist record containing the node closest to the coordinate and it's distance to coordinate.
     */
    private nearestNodeDist _findNearestNodeDist(Coordinate coordinate, List<Node> nodes) {
        Node nearestNode = null;
        double nearestDist = Double.MAX_VALUE;

        for (Node n : nodes) {
            Coordinate nodeCoord = n.getCoordinate();
            double dist = UtilityTools.euclideanDistance(coordinate,nodeCoord);

            if (dist < nearestDist) {
                nearestNode = n;
                nearestDist = dist;
            }
        }
        return new nearestNodeDist(nearestNode, nearestDist);
    }

    /**
     * A record containing a node and a distance. Used by _findNearestNodeDist() and getNearestNeighbor()
     * @param node a Node
     * @param dist its distance to from target
     */
    record nearestNodeDist(Node node, double dist) { }

    /**
     * Inserts an element in the R-Tree for spatial querying.
     * Indexes the element into the TreeNode minimum-bounding-rectangle that needs the least enlargement to fit the inserted element.
     * After insertion, the methods calls updateSubtreeMinZoom() for efficient filtering of the subtree on Tree.search(), then updates the minimum-bounding-rectangle of the TreeNode to accomodate the inserted element.
     * If the TreeNode overflows (has more entries than allowed by the max-value of the Tree), the TreeNode is split into to new TreeNodes.
     *
     * @see updateSubtreeMinZoom
     * @see chooseLeaf
     * @see updateTreeNodeMbr
     * @see splitNode
     * @param element The OSM-element to be inserted into the R-Tree.
     */
    public void insert(OsmElement element) {
        if (element == null || element.getMbr() == null) return;
        BoundingBox mbr = element.getMbr();
        if (Double.isNaN(mbr.minLat()) || Double.isNaN(mbr.minLon()) || Double.isNaN(mbr.maxLat()) || Double.isNaN(mbr.maxLon())) return;
        if (root == null) {
            root = new TreeNode(true);
        }

        List<TreeNode> path = new ArrayList<>();
        TreeNode leaf = chooseLeaf(root, element.getMbr(), path);
        leaf.entries.add(new LeafEntry(element));

        updateSubtreeMinZoom(element.getMinZoomLevel(), path);

        updateTreeNodeMbr(leaf);

        TreeNode splitResult = null;
        if (leaf.isOverflowing(max)) {
            splitResult = splitNode(leaf);
        }

        adjustTree(path, leaf, splitResult);

        this.mbr = (root != null) ? root.getMbr() : null;
    }

    /**
     * Helper function called by Tree.insert(), to update the value of subtreeMinZoom of all TreeNodes in a subtree, based on the smallest zoomValue of its ancestors.
     * TreeNode the value of the entry with the smallest zoom level in the subtree, so an entire subtree can be disregarded if it contains no elements that are visible in relation to the current zoom level.
     * @param minZoomLevel
     * @param path
     */
    private void updateSubtreeMinZoom(double minZoomLevel, List<TreeNode> path) {
        boolean hasUpdated = path.getLast().updateSubtreeMinZoom(minZoomLevel);

        if (!hasUpdated) {
            return;
        }

        for (int i = path.size() - 2; i >= 0; i--) {
            TreeNode prev = path.get(i + 1);
            TreeNode curr = path.get(i);
            hasUpdated = curr.updateSubtreeMinZoom(prev.getSubtreeMinZoom());

            if (!hasUpdated) {
                return;
            }
        }
    }

    private void updateSubtreeMinZoom(TreeNode node) {
        node.resetSubtreeMinZoom();
        for (TreeEntry entry : node.entries) {
            switch (entry) {
                case NodeEntry nodeEntry -> node.updateSubtreeMinZoom(nodeEntry.child().getSubtreeMinZoom());
                case LeafEntry leafEntry -> node.updateSubtreeMinZoom(leafEntry.element().getMinZoomLevel());
            }
        }
    }

    private TreeNode chooseLeaf(TreeNode node, BoundingBox mbr, List<TreeNode> path) {
        path.add(node);

        if (node.isLeaf()) {
            return node;
        }

        TreeNode bestChild = null;
        double minAreaIncr = Double.MAX_VALUE;
        double minArea = Double.MAX_VALUE;

        for (TreeEntry entry : node.entries) {
            if (entry instanceof NodeEntry nodeEntry) {
                double areaIncr = nodeEntry.getMbr().areaIncreaseNeeded(mbr);
                double area = entry.getMbr().area();

                if (areaIncr < minAreaIncr || (areaIncr == minAreaIncr && area < minArea)) {
                    bestChild = nodeEntry.child();
                    minAreaIncr = areaIncr;
                    minArea = area;
                }
            }
        }

        return chooseLeaf(bestChild, mbr, path);
    }

    private void adjustTree(List<TreeNode> path, TreeNode child, TreeNode sibling) {
        for (int i = path.size() - 2; i >= 0; i--) {
            TreeNode parent = path.get(i);

            for (int j = 0; j < parent.entries.size(); j++) {
                TreeEntry entry = parent.entries.get(j);
                if (entry instanceof NodeEntry nodeEntry && nodeEntry.child() == child) {
                    parent.entries.set(j, new NodeEntry(child.getMbr(), child));
                    break;
                }
            }

            if (sibling != null) {
                parent.entries.add(new NodeEntry(sibling.getMbr(), sibling));

                if (parent.isOverflowing(max)) {
                    sibling = splitNode(parent);
                } else {
                    sibling = null;
                }
            }

            updateTreeNodeMbr(parent);
            child = parent;
        }

        // If sibling != null, then the root has been split
        if (sibling != null) {
            TreeNode newRoot = new TreeNode(false);
            newRoot.entries.add(new NodeEntry(root.getMbr(), root));
            newRoot.entries.add(new NodeEntry(sibling.getMbr(), sibling));

            newRoot.updateSubtreeMinZoom(root.getSubtreeMinZoom());
            newRoot.updateSubtreeMinZoom(sibling.getSubtreeMinZoom());

            root = newRoot;
            updateTreeNodeMbr(root);
        }
    }

    private TreeNode splitNode(TreeNode node) {
        List<TreeEntry> allEntries = new ArrayList<>(node.entries);

        SeedPack seeds = pickSeeds(allEntries);

        List<TreeEntry> left = new ArrayList<>();
        List<TreeEntry> right = new ArrayList<>();
        left.add(seeds.left);
        right.add(seeds.right);
        allEntries.removeAll(seeds.all());

        while (!allEntries.isEmpty()) {
            if (left.size() + allEntries.size() == min) {
                left.addAll(allEntries);
                break;
            }
            if (right.size() + allEntries.size() == min) {
                right.addAll(allEntries);
                break;
            }

            TreeEntry next = pickNext(allEntries, left, right);
            allEntries.remove(next);

            BoundingBox leftMbr = computeMBR(left);
            BoundingBox rightMbr = computeMBR(right);
            double leftAreaIncr = leftMbr.areaIncreaseNeeded(next.getMbr());
            double rightAreaIncr = rightMbr.areaIncreaseNeeded(next.getMbr());

            if (leftAreaIncr < rightAreaIncr) {
                left.add(next);
            } else if (rightAreaIncr < leftAreaIncr) {
                right.add(next);
            } else {
                if (leftMbr.area() < rightMbr.area()) {
                    left.add(next);
                } else if (rightMbr.area() < leftMbr.area()) {
                    right.add(next);
                } else {
                    if (left.size() < right.size()) {
                        left.add(next);
                    } else if (right.size() < left.size()) {
                        right.add(next);
                    } else {
                        if (Math.random() < 0.5) {
                            left.add(next);
                        } else {
                            right.add(next);
                        }
                    }
                }
            }
        }

        node.entries.clear();
        node.entries.addAll(left);
        updateTreeNodeMbr(node);

        TreeNode newNode = new TreeNode(node.isLeaf());
        newNode.entries.addAll(right);
        updateTreeNodeMbr(newNode);

        updateSubtreeMinZoom(node);
        updateSubtreeMinZoom(newNode);

        return newNode;
    }

    private SeedPack pickSeeds(List<TreeEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("pickSeeds needs at least 2 entries");
        }
        SeedPack seeds = null;
        double maxDeadSpace = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                TreeEntry a = entries.get(i);
                TreeEntry b = entries.get(j);

                double containerArea = computeMBR(List.of(a, b)).area();

                double aArea = a.getMbr().area();
                double bArea = b.getMbr().area();
                double deadSpace = containerArea - aArea - bArea;
                if (Double.isNaN(deadSpace)) continue;

                if (deadSpace > maxDeadSpace) {
                    maxDeadSpace = deadSpace;
                    seeds = new SeedPack(a, b);
                }
            }
        }
        if (seeds == null) {
            seeds = new SeedPack(entries.get(0), entries.get(1));
        }

        return seeds;
    }

    private TreeEntry pickNext(List<TreeEntry> entries, List<TreeEntry> left, List<TreeEntry> right) {
        if (entries.size() == 1) {
            return entries.getFirst();
        }

        double minAreaIncr = Double.MAX_VALUE;
        TreeEntry next = null;

        for (TreeEntry e : entries) {
            double leftAreaIncr = computeMBR(left).areaIncreaseNeeded(e.getMbr());
            double rightAreaIncr = computeMBR(right).areaIncreaseNeeded(e.getMbr());
            if (leftAreaIncr < minAreaIncr || rightAreaIncr < minAreaIncr) {
                next = e;
                minAreaIncr = Math.min(leftAreaIncr, rightAreaIncr);
            }
        }

        return next;
    }

    private BoundingBox computeMBR(List<TreeEntry> entries) {
        BoundingBox result = entries.getFirst().getMbr().copy();
        for (int i = 1; i < entries.size(); i++) {
            result = result.getExpanded(entries.get(i).getMbr());
        }
        return result;
    }

    private BoundingBox computeMBR(TreeNode node) {
        return computeMBR(node.entries);
    }

    record SeedPack(TreeEntry left, TreeEntry right) {
        public List<TreeEntry> all() {
            return List.of(left, right);
        }
    }
}
