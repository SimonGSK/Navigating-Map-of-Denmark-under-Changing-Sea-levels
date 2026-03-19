package models.osm;

import Interfaces.Drawable;

import java.util.HashMap;


public abstract class Element implements Drawable {
    final private long id;
    private HashMap<String, String> tags;
    private double area;

    public Element(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }


    protected String addTag(String key, String value) {
        if (tags == null) {
            tags = new HashMap<>();
        }

        return tags.putIfAbsent(key, value);
    }


    protected boolean modifyTag(String key, String value) {
        if (tags == null || !tags.containsKey(key)) {
            return false;
        }

        tags.put(key, value);
        return true;
    }


    protected String getTag(String key) {
        if (tags == null) {
            return null;
        }

        return tags.get(key);
    }


    protected boolean contains(String key) {
        if (tags == null) {
            return false;
        }

        return tags.containsKey(key);
    }


    protected HashMap<String, String> getTags() {
        if (tags == null) {
            return null;
        }
        return new HashMap<>(tags);
    }

    public double getArea() {
        return this.area;
    }

    protected void setArea(double area) {
        this.area = area;
    }


}
