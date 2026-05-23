package models.osm;


import enums.ElementType;

import java.io.Serializable;

public class Member implements Serializable {
    private final Element element;
    private final String role;
    private final ElementType type;

    /**
     * Constructs a Member which represents a constituent part of an OSM Relation.
     *
     * @param element The physical OSM element (Node, Way, or Relation).
     * @param type The type of the element.
     * @param role The role this member serves within its parent relation (e.g. "inner", "outer").
     */
    public Member(Element element, ElementType type, String role) {
        this.element = element;
        this.type = type;
        this.role = role;
    }

    /**
     * Gets the OSM Element wrapped inside inherently safely.
     *
     * @return The underlying geographic mapping component.
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the role of the Member.
     *
     * @return The role this member serves within its parent relation (e.g. "inner", "outer").
     */
    public String getRole() {
        return role;
    }

    /**
     * Gets the type of the Member.
     *
     * @return The type.
     */
    public ElementType getType() {
        return type;
    }
}