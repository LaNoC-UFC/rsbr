package util;

import java.util.*;

public class Bridge {
    private int count;
    private int[] pre; // pre[v] = order in which dfs examines v
    private int[] low; // low[v] = lowest preorder of any vertex connected to v
    private List<Edge> bridges;

    public Bridge(Graph graph) {
        assert graph != null : "Null pointer for the graph!";
        bridges = new ArrayList<>();
        low = new int[graph.getVertices().size()];
        pre = new int[graph.getVertices().size()];
        count = 0;
        for (int v = 0; v < graph.getVertices().size(); v++) {
            low[v] = pre[v] = -1;
        }
        for (Vertex v : graph.getVertices()) {
            if (pre[graph.indexOf(v)] == -1) {
                dfs(graph, v, v);
            }
        }
    }

    public List<Edge> bridges() {
        return bridges;
    }

    private void dfs(Graph graph, Vertex u, Vertex v) {
        if (graph == null) {
            return;
        }

        low[graph.indexOf(v)] = pre[graph.indexOf(v)] = count++;
        for(Edge e : graph.adjunctsOf(v)) {
            Vertex w = e.destination();
            if (pre[graph.indexOf(w)] == -1) {
                dfs(graph, v, w);
                low[graph.indexOf(v)] = Math.min(low[graph.indexOf(v)], low[graph.indexOf(w)]);
                if (low[graph.indexOf(w)] == pre[graph.indexOf(w)]) {
                    bridges.add(e);
                }
            }
            else {
                if (!w.equals(u)) {
                    low[graph.indexOf(v)] = Math.min(low[graph.indexOf(v)], low[graph.indexOf(w)]);
                }
            }
        }
    }
}