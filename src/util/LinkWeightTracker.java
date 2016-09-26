package util;

import java.util.*;

public class LinkWeightTracker {
    private Map<Edge,Double> weights;

    public LinkWeightTracker(Graph g) {
        weights = new HashMap<>();
        for(Edge e : g.getEdges()) {
            weights.put(e, 0.0);
        }
    }

    public void add(Path p) {
        for(Edge e : p.edges()) {
            double newWeight = weights.get(e) + 1.0;
            weights.put(e, newWeight);
        }
    }

    public void addAll(Collection<Path> paths) {
        for(Path p : paths) {
            add(p);
        }
    }

    void remove(Path p) {
        for(Edge e : p.edges()) {
            double newWeight = weights.get(e) - 1.0;
            assert 0 <= newWeight;
            weights.put(e, newWeight);
        }
    }

    public void removeAll(Collection<Path> paths) {
        for(Path p : paths) {
            remove(p);
        }
    }

    double weight(Edge e) {
        return weights.get(e);
    }

    public double weight(Path p) {
        double pathWeight = 0.0;
        for(Edge e : p.edges()) {
            pathWeight += weight(e);
        }
        return pathWeight;
    }

    Collection<Double> edgesWeight() {
        return weights.values();
    }
}
