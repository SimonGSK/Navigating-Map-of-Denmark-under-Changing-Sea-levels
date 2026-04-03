package models.osm;


public class Member {
    private final Element element;
    private final String role;

    public Member(Element element, String role) {
        this.element = element;
        this.role = role;
    }
    public Element getElement() {
        return element;
    }
    public String getRole() {
        return role;
    }

}
