package util;

import java.util.*;

public class LinkWeightTracker {
    private Map<Edge, Double> edgesWeights;
    private Map<Path, Double> pathsVolume;
    private Graph graph;

    public LinkWeightTracker(Graph g) {
        edgesWeights = new HashMap<>();
        pathsVolume = new HashMap<>();
        graph = g;
        for (Edge e : g.getEdges()) {
            edgesWeights.put(e, 0.0);
        }
    }

    public LinkWeightTracker(Graph g, Map<Path, Double> volumes) {
        this(g);
        pathsVolume = volumes;
    }

    public void add(Path p) {
        for (Edge e : edgesOf(p)) {
            double newWeight = edgesWeights.get(e) + volume(p);
            edgesWeights.put(e, newWeight);
        }
    }

    public void addAll(Collection<Path> paths) {
        for (Path p : paths) {
            add(p);
        }
    }

    public void removeAll(Collection<Path> paths) {
        for (Path p : paths) {
            remove(p);
        }
    }

    void remove(Path p) {
        for (Edge e : edgesOf(p)) {
            double newWeight = edgesWeights.get(e) - volume(p);
            assert 0 <= newWeight;
            edgesWeights.put(e, newWeight);
        }
    }

    private double volume(Path p) {
        return pathsVolume.getOrDefault(p, 1.0);
    }

    public double weight(Path p) {
        double pathWeight = 0.0;
        for (Edge e : edgesOf(p)) {
            pathWeight += weight(e);
        }
        return pathWeight;
    }

    double weight(Edge e) {
        return edgesWeights.get(e);
    }

    public Collection<Double> edgesWeight() {
        return edgesWeights.values();
    }

    private Collection<Edge> edgesOf(Path p) {
        List<Edge> edges = new ArrayList<>();
        for (int i = 0; i < p.edgesCount(); i++) {
            edges.add(graph.adjunct(p.get(i), p.get(i + 1)));
        }
        return edges;
    }
}
