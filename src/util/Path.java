package util;

import java.util.*;

public class Path extends ArrayList<Vertex> implements Comparable<Path> {

    private static final long serialVersionUID = 1L;

    public static class MinWeightComparator implements Comparator<Path> {
        private LinkWeightTracker lwTracker;

        public MinWeightComparator(LinkWeightTracker tracker) {
            lwTracker = tracker;
        }

        @Override
        public int compare(Path p0, Path p1) {
            if (lwTracker.weight(p0) < lwTracker.weight(p1)) return -1;
            if (lwTracker.weight(p0) > lwTracker.weight(p1)) return +1;
            return 0;
        }
    }

    public static class SrcDstComparator implements Comparator<Path> {

        @Override
        public int compare(Path p0, Path p1) {
            int src = p0.src().name().compareTo(p1.src().name());
            int dst = p0.dst().name().compareTo(p1.dst().name());
            return (src != 0) ? src : dst;
        }
    }

    public static class MaxWeightComparator implements Comparator<Path> {
        private LinkWeightTracker lwTracker;

        public MaxWeightComparator(LinkWeightTracker tracker) {
            lwTracker = tracker;
        }

        @Override
        public int compare(Path p0, Path p1) {
            if (lwTracker.weight(p0) < lwTracker.weight(p1)) return +1;
            if (lwTracker.weight(p0) > lwTracker.weight(p1)) return -1;
            return 0;
        }
    }

    public Path() {
        super();
    }

    public Path(Path p) {
        super(p);
    }

    public int edgesCount() {
        return this.size() - 1;
    }

    public Vertex dst() {
        return this.get(this.size() - 1);
    }

    public Vertex src() {
        return this.get(0);
    }

    @Override
    public int compareTo(Path other) {
        if (this.edgesCount() < other.edgesCount())
            return -1;

        if (this.edgesCount() > other.edgesCount())
            return 1;
        return 0;
    }

    @Override
    public String toString() {
        String pathLine = "";
        for (Vertex v : this) {
            pathLine += " " + v.name();
        }
        return pathLine;
    }
}
