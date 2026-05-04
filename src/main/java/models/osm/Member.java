package models.osm;


import enums.ElementType;

import java.io.Serializable;

public class Member implements Serializable {
    private final Element element;
    private final String role;
    private final ElementType type;

    public Member(Element element, ElementType type, String role) {
        this.element = element;
        this.type = type;
        this.role = role;
    }
    public Element getElement() {
        return element;
    }
    public String getRole() {
        return role;
    }
    public ElementType getType() {
        return type;
    }
}