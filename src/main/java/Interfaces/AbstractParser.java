package Interfaces;

import models.geometry.BoundingBox;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;

import java.io.IOException;
import java.util.HashMap;

public abstract class AbstractParser<T> {
    protected String filePath;
    protected T data;

    public String getAttribute(String str, String key) {
        String pattern = key + "=\"";
        int start = str.indexOf(pattern);
        if (start == -1) {
            return null;
        }
        int valueStart = start + pattern.length();
        int valueEnd = str.indexOf('"', valueStart);
        return str.substring(valueStart, valueEnd);
    }

    public double getAttributeDouble(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return Double.NaN;
        }
        return Double.parseDouble(val);
    }

    public long getAttributeLong(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return 0L;
        }
        return Long.parseLong(val);
    }

    public abstract void parse(String filePath) throws IOException;

    public T getData() {
        return data;
    }

    public String getFilePath() {
        return filePath;
    }
}
