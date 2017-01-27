package util;

import java.util.*;

public class Graph {
    private List<Vertex> vertices;
    private List<Edge> edges;
    private Map<Vertex, Collection<Edge>> adjuncts;
    private int columns;
    private int rows;

    public Graph(int rows, int columns) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        adjuncts = new HashMap<>();
        this.columns = columns;
        this.rows = rows;
    }

    public boolean hasIsolatedCores() {
        Set<Vertex> reachedVertices = new HashSet<>();
        reachAdjuncts(vertex("0.0"), reachedVertices);
        return !(reachedVertices.size() == vertices.size());
    }

    private void reachAdjuncts(Vertex vertex, Set<Vertex> reachable) {
        if (reachable.contains(vertex)) {
            return;
        }
        reachable.add(vertex);
        for (Edge adj : adjunctsOf(vertex)) {
            Vertex neigh = adj.destination();
            reachAdjuncts(neigh, reachable);
        }
    }

    public List<Vertex> getVertices() {
        return this.vertices;
    }

    public List<Edge> getEdges() {
        return new ArrayList<>(this.edges);
    }

    public Collection<Edge> adjunctsOf(Vertex v) {
        return this.adjuncts.get(v);
    }

    public Edge adjunct(Vertex src, Vertex dst) {
        for (Edge edge : adjunctsOf(src)) {
            if (edge.destination().equals(dst)) {
                return edge;
            }
        }
        throw new RuntimeException("There isn't adjunct between " + src.name() + "and" + dst.name());
    }

    public Edge adjunctOf(Vertex v, Character color) {
        for (Edge edge : adjunctsOf(v)) {
            if (edge.color().equals(color)) {
                return edge;
            }
        }
        throw new RuntimeException("Vertex " + v.name() + " has no " + color + "output port");
    }

    public Vertex vertex(String name) {
        for (Vertex v : this.vertices) {
            if (v.name().equals(name)) {
                return v;
            }
        }
        throw new RuntimeException("Vertex: " + name + " was not found");
    }

    void addVertex(String name) {
        Vertex v = new Vertex(name);
        vertices.add(v);
        adjuncts.put(v, new ArrayList<>());
    }

    void addEdge(Vertex src, Vertex dst, Character color) {
        addEdge(new Edge(src, dst, color));
    }

    void addEdge(Edge toAdd) {
        adjuncts.get(toAdd.source()).add(toAdd);
        edges.add(toAdd);
    }

    void removeEdge(Edge toRemove) {
        adjuncts.get(toRemove.source()).remove(toRemove);
        edges.remove(toRemove);
    }

    @Override
    public String toString() {
        String r = "";
        System.out.println("Graph:");
        for (Vertex u : vertices) {
            r += u.name() + " -> ";
            for (Edge e : adjunctsOf(u)) {
                Vertex v = e.destination();
                r += v.name() + e.color() + ", ";
            }
            r += "\n";
        }
        return r;
    }

    public int columns() {
        return columns;
    }

    public int rows() {
        return rows;
    }

    public int indexOf(Vertex v) {
        return indexOf(v.name());
    }

    private int indexOf(String xy) {
        int x = Integer.parseInt(xy.split("\\.")[0]);
        int y = Integer.parseInt(xy.split("\\.")[1]);
        return x + y * this.columns();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Graph))
            return false;

        Graph that = (Graph) o;
        if (!this.vertices.equals(that.vertices))
            return false;
        return this.edges.equals(that.edges);
    }

    @Override
    public int hashCode() {
        int result = vertices.hashCode();
        result = 31 * result + edges.hashCode();
        return result;
    }
}
