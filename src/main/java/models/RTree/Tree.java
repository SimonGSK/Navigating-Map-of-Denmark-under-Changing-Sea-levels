package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.util.*;

public class Tree {
    private TreeNode root;
    private final int min = 1; // Must be >= 1
    private final int max = 30; // Must be
    private BoundingBox mbr;
    private final TreeData treeData;

    public Tree(BoundingBox mbr, Map<Long,Node> nodeMap, Map<Long, Way> wayMap, Map<Long, Relation> relationMap) {
        if (mbr == null || nodeMap == null || wayMap == null || relationMap == null) {
            throw new RuntimeException("nodeWay, wayMap, and relationMap must not be null");
        }
        this.mbr = mbr;
        this.treeData = new TreeData(nodeMap, wayMap, relationMap);
        this.treeData.forEach(this::insert);
    }

    public void updateTreeNodeMbr(TreeNode node) {
        if (node == null || node.entries == null || node.entries.isEmpty()) {
            return;
        }
        node.setMbr(computeMBR(node.entries));
    }

    public BoundingBox getMbr() {
        return mbr;
    }

    public SearchResults search(BoundingBox searchArea) {
        SearchResults searchResults = new SearchResults();
        if (root != null) {
            searchRecursive(root, searchArea, searchResults);
        }
        searchResults.sort();
        return searchResults;
    }

    private void searchRecursive(TreeNode node, BoundingBox searchArea, SearchResults searchResults) {
        for (TreeEntry entry : node.entries) {
            if (entry.overlaps(searchArea)) {
                switch (entry) {
                    case LeafEntry leaf -> {
                        // TODO: Implement LOD-logic here
                        searchResults.add(leaf.element().getType(), leaf.element());
                    }
                    case NodeEntry nonLeaf -> searchRecursive(nonLeaf.child(), searchArea, searchResults);
                }
            }
        }
    }

    public Node getNearestNode(Coordinate cursor) {
        if (root == null) {
            return null;
        }

        List<TreeNode> path = new ArrayList<>();
        TreeNode nearestTreeNode = chooseLeaf(root,new BoundingBox(cursor.getLat(), cursor.getLon(), cursor.getLat(),cursor.getLon()),path);

        List<Node> nodeList = nearestTreeNode.entries.stream().map(e -> (LeafEntry) e).map(LeafEntry::element).filter(e -> e.getType() == ElementType.node).map(e -> (Node) e).toList();

        Node nearestNode = null;
        double nearestDist = Float.POSITIVE_INFINITY;

        for (Node n : nodeList) {
            Coordinate center = n.getCoordinate();
            double dist = Math.sqrt(
                    Math.pow(cursor.getLat() + center.getLat(),2)
                    + Math.pow(cursor.getLon() + center.getLon(),2)
            );

            if (dist < nearestDist) {
                nearestNode = n;
                nearestDist = dist;
            }
        }

        return nearestNode;
    }

    public void insert(Element element) {
        if (root == null) {
            root = new TreeNode(true);
        }

        List<TreeNode> path = new ArrayList<>();
        TreeNode leaf = chooseLeaf(root, element.getMbr(), path);
        leaf.entries.add(new LeafEntry(element));
        updateTreeNodeMbr(leaf);

        TreeNode splitResult = null;
        if (leaf.isOverflowing(max)) {
            splitResult = splitNode(leaf);
        }

        adjustTree(path, leaf, splitResult);

        this.mbr = (root != null) ? root.getMbr() : null;
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

        return newNode;
    }

    private SeedPack pickSeeds(List<TreeEntry> entries) {
        if (entries == null || entries.size() < 2) {
            throw new IllegalArgumentException("pickSeeds needs at least 2 entries");
        }
        SeedPack seeds = null;
        double maxDeadSace = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < entries.size() - 1; i++) {
            for (int j = i + 1; j < entries.size(); j++) {
                TreeEntry a = entries.get(i);
                TreeEntry b = entries.get(j);

                double containerArea = computeMBR(List.of(a, b)).area();

                double aArea = a.getMbr().area();
                double bArea = b.getMbr().area();
                double deadSpace = containerArea - aArea - bArea;

                if (deadSpace > maxDeadSace) {
                    maxDeadSace = deadSpace;
                    seeds = new SeedPack(a, b);
                }
            }
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
