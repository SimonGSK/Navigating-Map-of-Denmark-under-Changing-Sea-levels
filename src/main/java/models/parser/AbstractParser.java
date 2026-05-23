package models.parser;

import java.io.IOException;

/**
 * Base parser with simple XML attribute helpers.
 */
public abstract class AbstractParser<T> {
    protected String filePath;
    protected T data;

    /**
     * Reads a string attribute from a tag line.
     * @param str tag line
     * @param key attribute name
     * @return attribute value or null
     */
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

    /**
     * Reads a double attribute from a tag line.
     * @param s tag line
     * @param key attribute name
     * @return attribute value or NaN
     */
    public double getAttributeDouble(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return Double.NaN;
        }
        return Double.parseDouble(val);
    }

    /**
     * Reads a long attribute from a tag line.
     * @param s tag line
     * @param key attribute name
     * @return attribute value or 0
     */
    public long getAttributeLong(String s, String key) {
        String val = getAttribute(s, key);
        if (val == null) {
            return 0L;
        }
        return Long.parseLong(val);
    }

    /**
     * Parses the file at the given path.
     * @param filePath path to parse
     * @throws IOException when reading fails
     */
    public abstract void parse(String filePath) throws IOException;

    /**
     * @return parsed data instance
     */
    public T getData() {
        return data;
    }
}
