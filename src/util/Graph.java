package util;

import java.util.*;

public class Graph {
    private ArrayList<Vertex> vertices;
    private ArrayList<Edge> edges;
    private Map<Vertex, Collection<Edge>> adjuncts;
    private int dimX;
    private int dimY;

    public Graph(int rows, int columns) {
        vertices = new ArrayList<>();
        edges = new ArrayList<>();
        adjuncts = new HashMap<>();
        dimX = columns;
        dimY = rows;
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

    public ArrayList<Vertex> getVertices() {
        return this.vertices;
    }

    public ArrayList<Edge> getEdges() {
        return this.edges;
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
        System.out.println("ERROR : There isn't adjunct between " + src.name() + "and" + dst.name());
        return null;
    }

    public Edge adjunctOf(Vertex v, Character color) {
        for (Edge edge : adjunctsOf(v)) {
            if (edge.color().equals(color)) {
                return edge;
            }
        }
        System.out.println("ERROR : There isn't a Op " + color + "?");
        return null;
    }

    public Vertex vertex(String name) {
        Vertex vertex = null;
        for (Vertex v : this.vertices) {
            if (v.name().equals(name)) {
                vertex = v;
            }
        }

        if (vertex == null) {
            System.out.println("Vertex: " + name + " was not found");
            return null;
        }
        return vertex;
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

    public int dimX() {
        return dimX;
    }

    public int dimY() {
        return dimY;
    }

    public int indexOf(Vertex v) {
        return indexOf(v.name());
    }

    private int indexOf(String xy) {
        int x = Integer.parseInt(xy.split("\\.")[0]);
        int y = Integer.parseInt(xy.split("\\.")[1]);
        return x + y * this.dimX();
    }
}
