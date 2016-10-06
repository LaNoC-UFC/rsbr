package util;

import java.util.*;

public class GraphRestrictions {
    private Map<Vertex, Map<Character, Set<Character>>> restrictions;

    public GraphRestrictions(Graph g) {
        restrictions = new HashMap<>();
    }

    public void addRestriction(Vertex v, Character op, Character rest) {
        Map<Character, Set<Character>> vertexRestrictions = getOrInit(v);
        vertexRestrictions.get(op).add(rest);
    }

    public Set<Character> getRestriction(Vertex v, Character op) {
        if(!restrictions.containsKey(v)) {
            return new HashSet<>();
        }
        Map<Character, Set<Character>> vertexRestrictions = restrictions.get(v);
        return vertexRestrictions.get(op);
    }

    private Map<Character, Set<Character>> getOrInit(Vertex v) {
        if(!restrictions.containsKey(v)) {
            Map<Character, Set<Character>> rest = new HashMap<>();
            rest.put('I', new HashSet<>());
            rest.put('N', new HashSet<>());
            rest.put('S', new HashSet<>());
            rest.put('E', new HashSet<>());
            rest.put('W', new HashSet<>());
            restrictions.put(v, rest);
        }
        return restrictions.get(v);
    }
    public int size() {
        return restrictions.size();
    }
}
