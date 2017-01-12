package paths_gen;

import util.*;

import java.util.*;

public class PathFinder {
    private Graph graph;
    private GraphRestrictions restrictions;

    public PathFinder(Graph g, GraphRestrictions restrictions) {
        this.graph = g;
        this.restrictions = restrictions;
    }

    public List<List<Path>> minimalPathsForAllPairs() {
        List<Path> result = new ArrayList<>();
        Set<String> alreadyFoundPairs = new HashSet<>();
        int nPairs = graph.getVertices().size() * (graph.getVertices().size() - 1);
        for (List<Path> foundPaths = oneHopPaths(); alreadyFoundPairs.size() < nPairs; foundPaths = minimalPaths(advanceOneHop(foundPaths), alreadyFoundPairs)) {
            updateFoundPairs(alreadyFoundPairs, foundPaths);
            result.addAll(foundPaths);
        }
        return divideByPair(result);
    }

    public List<Path> allPathsBetweenVertices(Vertex src, Vertex dst) {
        List<Path> result = new ArrayList<>();
        for (List<Path> foundPaths = oneHopPathsFrom(src); !foundPaths.isEmpty(); foundPaths = advanceOneHop(foundPaths)) {
            result.addAll(pathsWithDestination(foundPaths, dst));
            foundPaths.removeAll(result);
        }
        Collections.sort(result);
        return result;
    }

    private List<Path> oneHopPaths() {
        List<Path> result = new ArrayList<>();
        graph.getVertices().forEach(source -> result.addAll(oneHopPathsFrom(source)));
        return result;
    }

    private List<Path> oneHopPathsFrom(Vertex src) {
        List<Path> result = new ArrayList<>();
        for (Edge e : graph.adjunctsOf(src)) {
            Vertex dst = e.destination();
            if (restrictions.turnIsForbidden(src, 'I', graph.adjunct(src, dst).color())) {
                continue;
            }
            Path p = new Path();
            p.add(src);
            p.add(dst);
            result.add(p);
        }
        return result;
    }

    private List<Path> advanceOneHop(List<Path> previouslyFoundPaths) {
        List<Path> result = new ArrayList<>();
        previouslyFoundPaths.forEach(p -> result.addAll(oneHopMore(p)));
        return result;
    }

    private List<Path> oneHopMore(Path p) {
        List<Path> result = new ArrayList<>();
        Vertex currentSrc = p.dst();
        Vertex predecessor = p.get(p.size() - 2);
        Character inputPort = graph.adjunct(currentSrc, predecessor).color();
        for (Edge e : graph.adjunctsOf(currentSrc)) {
            Vertex dst = e.destination();
            if (dst.equals(predecessor))
                continue;
            if (restrictions.turnIsForbidden(currentSrc, inputPort, graph.adjunct(currentSrc, dst).color()))
                continue;
            if (p.contains(dst)) {
                continue;
            }
            Path q = new Path(p);
            q.add(dst);
            result.add(q);
        }
        return result;
    }

    private List<Path> minimalPaths(List<Path> paths, Set<String> alreadyFoundPairs) {
        List<Path> result = new ArrayList<>();
        for (Path p : paths) {
            if (!alreadyFoundPairs.contains(pairDescriptor(p.src(), p.dst()))) {
                result.add(p);
            }
        }
        return result;
    }

    private List<Path> pathsWithDestination(List<Path> paths, Vertex dst) {
        List<Path> result = new ArrayList<>();
        for (Path path : paths) {
            if (path.dst().equals(dst)) {
                result.add(path);
            }
        }
        return result;
    }

    private void updateFoundPairs(Set<String> alreadyFoundPairs, List<Path> newPaths) {
        newPaths.forEach(p -> alreadyFoundPairs.add(pairDescriptor(p.src(), p.dst())));
    }

    private List<List<Path>> divideByPair(List<Path> paths) {
        // Sort by pair
        Collections.sort(paths, new Path.SrcDstComparator());
        // Then by length
        Collections.sort(paths);
        // Split by pair
        List<List<Path>> result = new ArrayList<>();
        int i = 0;
        while (i < paths.size()) {
            List<Path> bucket = samePairPaths(paths, i);
            result.add(bucket);
            i = paths.indexOf(bucket.get(bucket.size() - 1)) + 1;
        }
        return result;
    }

    private List<Path> samePairPaths(List<Path> paths, int starting_from) {
        List<Path> result = new ArrayList<>();
        for (int i = starting_from; i < paths.size(); i++) {
            Path p = paths.get(i);
            if (!pertainsTo(result, p))
                break;
            result.add(p);
        }
        return result;
    }

    private boolean pertainsTo(List<Path> bucket, Path tac) {
        if (bucket.isEmpty())
            return true;
        Path tic = bucket.get(0);
        return isSamePair(tic, tac);
    }

    private boolean isSamePair(Path tic, Path tac) {
        return tic.src().equals(tac.src()) && tic.dst().equals(tac.dst());
    }

    private String pairDescriptor(Vertex src, Vertex dst) {
        return src.name() + ":" + dst.name();
    }
}
