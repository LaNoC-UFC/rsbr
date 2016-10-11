package paths_gen;

import util.*;
import java.util.*;

public class PathFinder {
    private Graph graph;
    private GraphRestrictions restrictions;

    public PathFinder(Graph g, GraphRestrictions restrictions){
        this.graph = g;
        this.restrictions = restrictions;
    }

    public ArrayList<ArrayList<Path>> pathsComputation() {
        ArrayList<Path> result = new ArrayList<>();
        Set<String> alreadyFoundPairs = new HashSet<>();
        // N = 1 hop
        ArrayList<Path> oneHopPaths = computeOneHopPaths(alreadyFoundPairs);
        result.addAll(oneHopPaths);
        ArrayList<Path> previouslyFoundPaths = oneHopPaths;
        // N > 1 hop
        int nPairs = graph.getVertices().size() * (graph.getVertices().size() - 1);
        while (alreadyFoundPairs.size() < nPairs) {
            ArrayList<Path> valid = advanceOneHop(previouslyFoundPaths, alreadyFoundPairs);
            result.addAll(valid);
            previouslyFoundPaths = valid;
        }
        return divideByPair(result);
    }

    private ArrayList<Path> computeOneHopPaths(Set<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<>();
        for (Vertex src : graph.getVertices()) {
            for (Edge e : graph.adjunctsOf(src)) {
                Vertex dst = e.destination();
                if (restrictions.getRestriction(src, 'I').contains(graph.adjunct(src, dst).color())) {
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

    private ArrayList<Path> advanceOneHop(ArrayList<Path> previouslyFoundPaths, Set<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<>();
        for (Path p : previouslyFoundPaths) {
            result.addAll(advanceOneHop(p, alreadyFoundPairs));
        }
        for (Path p : result) {
            alreadyFoundPairs.add(pairDescriptor(p.src(), p.dst()));
        }
        return result;
    }

    private ArrayList<Path> advanceOneHop(Path p, Set<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<>();
        Vertex currentSrc = p.dst();
        Vertex predecessor = p.get(p.size() - 2);
        Character inputPort = graph.adjunct(currentSrc, predecessor).color();
        for (Edge e : graph.adjunctsOf(currentSrc)) {
            Vertex dst = e.destination();
            // going back
            if (dst == predecessor)
                continue;
            // going to forbidden direction
            if ( restrictions.getRestriction(currentSrc, inputPort).contains(graph.adjunct(currentSrc, dst).color()))
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

    private ArrayList<ArrayList<Path>> divideByPair(ArrayList<Path> paths) {
        // Sort by pair
        Collections.sort(paths, new Path.SrcDstComparator());
        // Then by length
        Collections.sort(paths);
        // Split by pair
        ArrayList<ArrayList<Path>> result = new ArrayList<>();
        int i = 0;
        while(i < paths.size()) {
            ArrayList<Path> bucket = samePairPaths(paths, i);
            result.add(bucket);
            i = paths.indexOf(bucket.get(bucket.size()-1)) + 1;
        }
        return result;
    }

    private ArrayList<Path> samePairPaths(ArrayList<Path> paths, int starting_from) {
        ArrayList<Path> result = new ArrayList<>();
        for (int i = starting_from; i < paths.size(); i++) {
            Path p = paths.get(i);
            if(!pertainsTo(result, p))
                break;
            result.add(p);
        }
        return result;
    }

    private boolean pertainsTo(ArrayList<Path> bucket, Path tac) {
        if(bucket.isEmpty())
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
