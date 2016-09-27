package util;

import java.util.*;

public class LinkWeightTracker {
    private Map<Edge, Double> edgesWeights;
    private Map<Path, Double> pathsVolume = null;

    public LinkWeightTracker(Graph g) {
        edgesWeights = new HashMap<>();
        for(Edge e : g.getEdges()) {
            edgesWeights.put(e, 0.0);
        }
    }

    public LinkWeightTracker(Graph g, Map<Path, Double> volumes) {
        this(g);
        pathsVolume = volumes;
    }

    public void add(Path p) {
        for(Edge e : p.edges()) {
            double newWeight = edgesWeights.get(e) + volume(p);
            edgesWeights.put(e, newWeight);
        }
    }

    public void addAll(Collection<Path> paths) {
        for(Path p : paths) {
            add(p);
        }
    }

    public void removeAll(Collection<Path> paths) {
        for(Path p : paths) {
            remove(p);
        }
    }

    void remove(Path p) {
        for(Edge e : p.edges()) {
            double newWeight = edgesWeights.get(e) - volume(p);
            assert 0 <= newWeight;
            edgesWeights.put(e, newWeight);
        }
    }

    private double volume(Path p) {
        return (null == pathsVolume) ? 1.0 : pathsVolume.get(p);
    }

    public double weight(Path p) {
        double pathWeight = 0.0;
        for(Edge e : p.edges()) {
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
}
