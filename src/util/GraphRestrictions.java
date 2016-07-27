package util;

import java.util.HashMap;

public class GraphRestrictions {
    private HashMap<Vertex, String[]> restrictions;

    public GraphRestrictions(Graph g) {
        restrictions = new HashMap<>();
    }

    public void addRestriction(Vertex v, String op, String rest) {
        String[] vertexRestrictions = getOrInit(v);
        switch (op) {
            case "I":
                vertexRestrictions[0] = vertexRestrictions[0] + "" + rest;
                break;
            case "N":
                vertexRestrictions[1] = vertexRestrictions[1] + "" + rest;
                break;
            case "S":
                vertexRestrictions[2] = vertexRestrictions[2] + "" + rest;
                break;
            case "E":
                vertexRestrictions[3] = vertexRestrictions[3] + "" + rest;
                break;
            case "W":
                vertexRestrictions[4] = vertexRestrictions[4] + "" + rest;
                break;
        }
    }

    public String getRestriction(Vertex v, String op) {
        if(!restrictions.containsKey(v))
            return "";
        String[] vertexRestrictions = restrictions.get(v);
        switch (op) {
            case "I":
                return vertexRestrictions[0];
            case "N":
                return vertexRestrictions[1];
            case "S":
                return vertexRestrictions[2];
            case "E":
                return vertexRestrictions[3];
            case "W":
                return vertexRestrictions[4];
            default:
                return null;
        }
    }

    private String[] getOrInit(Vertex v) {
        if(!restrictions.containsKey(v))
            restrictions.put(v, new String[]{"", "", "", "", ""});
        return restrictions.get(v);
    }

    public int size() {
        return restrictions.size();
    }
}
