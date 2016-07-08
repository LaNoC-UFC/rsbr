package util;

import java.util.HashMap;

public class GraphRestrictions {
    private HashMap<Vertice, String[]> restrictions;

    public GraphRestrictions(Graph g) {
        restrictions = new HashMap<>();
        for(Vertice v : g.getVertices())
            restrictions.put(v, new String[]{"", "", "", "", ""});
    }

    public void addRestriction(Vertice v, String op, String rest) {
        String[] verticeRestrictions = restrictions.get(v);
        switch (op) {
            case "I":
                verticeRestrictions[0] = verticeRestrictions[0] + "" + rest;
                break;
            case "N":
                verticeRestrictions[1] = verticeRestrictions[1] + "" + rest;
                break;
            case "S":
                verticeRestrictions[2] = verticeRestrictions[2] + "" + rest;
                break;
            case "E":
                verticeRestrictions[3] = verticeRestrictions[3] + "" + rest;
                break;
            case "W":
                verticeRestrictions[4] = verticeRestrictions[4] + "" + rest;
                break;
        }
    }

    public String getRestriction(Vertice v, String op) {
        String[] verticeRestrictions = restrictions.get(v);
        switch (op) {
            case "I":
                return verticeRestrictions[0];
            case "N":
                return verticeRestrictions[1];
            case "S":
                return verticeRestrictions[2];
            case "E":
                return verticeRestrictions[3];
            case "W":
                return verticeRestrictions[4];
            default:
                return null;
        }
    }
}
