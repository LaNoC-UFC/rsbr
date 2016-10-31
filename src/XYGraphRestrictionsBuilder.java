import util.*;

class XYGraphRestrictionsBuilder {

    static GraphRestrictions XYGraphRestrictions(Graph g) {
        GraphRestrictions rest = new GraphRestrictions(g);
        for (Vertex v : g.getVertices()) {
            rest.addRestriction(v, 'N', 'E');
            rest.addRestriction(v, 'N', 'W');
            rest.addRestriction(v, 'S', 'E');
            rest.addRestriction(v, 'S', 'W');
        }
        return rest;
    }
}
