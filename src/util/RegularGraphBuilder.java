package util;

public class RegularGraphBuilder {
    static public Graph generateGraph(int rows, int columns) {
        Graph result = new Graph(rows, columns);
        addVertices(result);
        addEdges(result);
        return result;
    }

    static private void addVertices(Graph graph) {
        for (int i = 0; i < graph.columns(); i++) {
            for (int j = 0; j < graph.rows(); j++) {
                String name = i + "." + j;
                graph.addVertex(name);
            }
        }
    }

    static private void addEdges(Graph graph) {
        for(int y = 0; y < graph.rows(); y++) {
            for(int x = 0; x < graph.columns(); x++) {
                Vertex current = graph.vertex(x + "." + y);
                if(contains(graph, x + "." + (y + 1)))
                    graph.addEdge(current, graph.vertex(x + "." + (y + 1)), TopologyKnowledge.colorFromTo(x + "." + y, x + "." + (y + 1)));
                if(contains(graph, x + "." + (y - 1)))
                    graph.addEdge(current, graph.vertex(x + "." + (y - 1)), TopologyKnowledge.colorFromTo(x + "." + y, x + "." + (y - 1)));
                if(contains(graph, (x + 1) + "." + y))
                    graph.addEdge(current, graph.vertex((x + 1) + "." + y), TopologyKnowledge.colorFromTo(x + "." + y, (x + 1) + "." + y));
                if(contains(graph, (x - 1) + "." + y))
                    graph.addEdge(current, graph.vertex((x - 1) + "." + y), TopologyKnowledge.colorFromTo(x + "." + y, (x - 1) + "." + y));
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
