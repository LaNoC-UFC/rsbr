package util;

public class RegularGraphBuilder {
    static public Graph generateGraph(int dimX, int dimY) {
        Graph result = new Graph(dimX, dimY);
        addVertices(result);
        addEdges(result);
        return result;
    }

    static private void addVertices(Graph graph) {
        for (int i = 0; i < graph.dimX(); i++) {
            for (int j = 0; j < graph.dimY(); j++) {
                String name = i + "." + j;
                graph.addVertex(name);
            }
        }
    }

    static private void addEdges(Graph graph) {
        for(int y = 0; y < graph.dimY(); y++) {
            for(int x = 0; x < graph.dimX(); x++) {
                Vertex current = graph.vertex(x + "." + y);
                if(contains(graph, x + "." + (y + 1)))
                    graph.addEdge(current, graph.vertex(x + "." + (y + 1)), EdgeColor.colorFromTo(x + "." + y, x + "." + (y + 1)));
                if(contains(graph, x + "." + (y - 1)))
                    graph.addEdge(current, graph.vertex(x + "." + (y - 1)), EdgeColor.colorFromTo(x + "." + y, x + "." + (y - 1)));
                if(contains(graph, (x + 1) + "." + y))
                    graph.addEdge(current, graph.vertex((x + 1) + "." + y), EdgeColor.colorFromTo(x + "." + y, (x + 1) + "." + y));
                if(contains(graph, (x - 1) + "." + y))
                    graph.addEdge(current, graph.vertex((x - 1) + "." + y), EdgeColor.colorFromTo(x + "." + y, (x - 1) + "." + y));
            }
        }
    }

    static private boolean contains(Graph g, String vertex) {
        for(Vertex v : g.getVertices()) {
            if (v.name().equals(vertex))
                return true;
        }
        return false;
    }
}
