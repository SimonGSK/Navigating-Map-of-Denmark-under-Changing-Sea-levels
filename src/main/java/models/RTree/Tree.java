package models.RTree;

import models.geometry.BoundingBox;
import models.geometry.Coordinate;
import models.osm.Element;
import models.osm.Node;

import java.util.ArrayList;
import java.util.List;

public class Tree {
    private TreeNode root;
    private int min = 1;
    private int max = 4;
    private BoundingBox mbr;

    public Tree(int min, int max) {
        if (min > Math.floorDiv(max, 2)) {
            throw new RuntimeException("min must be <= max / 2");
        }
        this.min = min;
        this.max = max;
    }

    public void updateTreeNodeMbr(TreeNode node) {
        if (node == null || node.entries == null || node.entries.isEmpty()) {
            return;
        }
        node.setMbr(computeMBR(node.entries));
    }

    public BoundingBox getMbr() {
        if (mbr == null) {
            mbr = computeMBR(root.entries);
        }
        return mbr;
    }

    public void setMbr(BoundingBox mbr) {
        this.mbr = mbr;
    }

    public List<Element> search(BoundingBox searchArea) {
        List<Element> results = new ArrayList<>();
        if (root != null) {
            searchRecursive(root, searchArea, results);
        }
        return results;
    }

    private void searchRecursive(TreeNode node, BoundingBox searchArea, List<Element> results) {
        for (TreeEntry entry : node.entries) {
            if (entry.overlaps(searchArea)) {
                switch (entry) {
                    case LeafEntry leaf -> results.add(leaf.data());
                    case NodeEntry nonLeaf -> searchRecursive(nonLeaf.child(), searchArea, results);
                }
            }
        }
    }

    public Node getNearestNode(Coordinate cursor) {
        float searchRadius = 1; // 1 lat/lon
        BoundingBox searchArea = new BoundingBox(cursor.getLat() - 1, cursor.getLon() - 1, cursor.getLat() + 1, cursor.getLon() + 1);
        
        List<Element> searchResults = search(searchArea).stream()
                .filter(e -> e instanceof Node)
                .toList();
        
        Node nearestNode = null;
        double nearestDist = Double.POSITIVE_INFINITY;
        
        for (Element e : searchResults) {
            if (e instanceof Node node) {
                double euclideanDist = Math.sqrt(Math.pow(nearestNode.getLat() - cursor.getLat(),2) + Math.pow(nearestNode.getLon() - cursor.getLon(),2));

                if (nearestNode == null || euclideanDist < nearestDist) {
                    nearestNode = node;
                    nearestDist = euclideanDist;
                }
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
        leaf.entries.add(new LeafEntry(element.getMbr(), element));
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
