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

    public List<List<Path>> pathsComputation() {
        List<Path> result = new ArrayList<>();
        Set<String> alreadyFoundPairs = new HashSet<>();
        // N = 1 hop
        List<Path> oneHopPaths = computeOneHopPaths(alreadyFoundPairs);
        result.addAll(oneHopPaths);
        List<Path> previouslyFoundPaths = oneHopPaths;
        // N > 1 hop
        int nPairs = graph.getVertices().size() * (graph.getVertices().size() - 1);
        while (alreadyFoundPairs.size() < nPairs) {
            List<Path> valid = advanceOneHop(previouslyFoundPaths, alreadyFoundPairs);
            result.addAll(valid);
            previouslyFoundPaths = valid;
        }
        return divideByPair(result);
    }

    private List<Path> computeOneHopPaths(Set<String> alreadyFoundPairs) {
        List<Path> result = new ArrayList<>();
        for (Vertex src : graph.getVertices()) {
            for (Edge e : graph.adjunctsOf(src)) {
                Vertex dst = e.destination();
                if (restrictions.turnIsForbidden(src, 'I', graph.adjunct(src, dst).color())) {
                    continue;
                }
                Path p = new Path();
                p.add(src);
                p.add(dst);
                result.add(p);
                alreadyFoundPairs.add(pairDescriptor(src, dst));
            }
        }
        return result;
    }

    private List<Path> advanceOneHop(List<Path> previouslyFoundPaths, Set<String> alreadyFoundPairs) {
        List<Path> result = new ArrayList<>();
        for (Path p : previouslyFoundPaths) {
            result.addAll(advanceOneHop(p, alreadyFoundPairs));
        }
        for (Path p : result) {
            alreadyFoundPairs.add(pairDescriptor(p.src(), p.dst()));
        }
        return result;
    }

    private List<Path> advanceOneHop(Path p, Set<String> alreadyFoundPairs) {
        List<Path> result = new ArrayList<>();
        Vertex currentSrc = p.dst();
        Vertex predecessor = p.get(p.size() - 2);
        Character inputPort = graph.adjunct(currentSrc, predecessor).color();
        for (Edge e : graph.adjunctsOf(currentSrc)) {
            Vertex dst = e.destination();
            // going back
            if (dst == predecessor)
                continue;
            if (restrictions.turnIsForbidden(currentSrc, inputPort, graph.adjunct(currentSrc, dst).color()))
                continue;
            // no mininal path
            if (alreadyFoundPairs.contains(pairDescriptor(p.src(), dst)))
                continue;
            Path q = new Path(p);
            q.add(dst);
            result.add(q);
        }
        return result;
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

    public List<Path> allPathsBetweenVertices(Vertex src, Vertex dst) {
        List<Path> result = new ArrayList<>();
        for (List<Path> foundPaths = computeOneHopPaths(src); !foundPaths.isEmpty(); foundPaths = advanceOneHopNoMinimal(foundPaths)) {
            result.addAll(pathsWithDestination(foundPaths, dst));
            foundPaths.removeAll(result);
        }
        Collections.sort(result);
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

    private List<Path> computeOneHopPaths(Vertex src) {
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

    private List<Path> advanceOneHopNoMinimal(List<Path> previouslyFoundPaths) {
        List<Path> result = new ArrayList<>();
        for (Path p : previouslyFoundPaths) {
            result.addAll(advanceOneHopNoMinimal(p));
        }
        return result;
    }

    private List<Path> advanceOneHopNoMinimal(Path p) {
        List<Path> result = new ArrayList<>();
        Vertex currentSrc = p.dst();
        Vertex predecessor = p.get(p.size() - 2);
        Character inputPort = graph.adjunct(currentSrc, predecessor).color();
        for (Edge e : graph.adjunctsOf(currentSrc)) {
            Vertex dst = e.destination();
            // going back
            if (dst == predecessor)
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
}
