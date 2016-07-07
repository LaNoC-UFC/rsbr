package rbr;

import util.Edge;
import util.Graph;
import util.Path;
import util.Vertice;

import java.util.ArrayList;
import java.util.Collections;

public class PathFinder {
    private Graph graph;

    public PathFinder(Graph g){
        graph = g;
    }

    public ArrayList<ArrayList<Path>> pathsComputation() {
        ArrayList<Path> result = new ArrayList<Path>();
        ArrayList<String> alreadyFoundPairs = new ArrayList<String>();
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

    private ArrayList<Path> computeOneHopPaths(ArrayList<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<Path>();
        for (Vertice src : graph.getVertices()) {
            for (Edge e : src.getAdj()) {
                Vertice dst = e.destination();
                if (src.getRestriction("I").contains(src.edge(dst).color()))
                    continue;
                Path p = new Path();
                p.add(src);
                p.add(dst);
                result.add(p);
                alreadyFoundPairs.add(pairDescriptor(src, dst));
            }
        }
        return result;
    }

    private ArrayList<Path> advanceOneHop(ArrayList<Path> previouslyFoundPaths, ArrayList<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<Path>();
        for (Path p : previouslyFoundPaths)
            result.addAll(advanceOneHop(p, alreadyFoundPairs));

        for (Path p : result) {
            // @ToDo change this to a Set to not worry with duplication
            if (!alreadyFoundPairs.contains(pairDescriptor(p.src(), p.dst()))) {
                alreadyFoundPairs.add(pairDescriptor(p.src(), p.dst()));
            }
        }
        return result;
    }

    private ArrayList<Path> advanceOneHop(Path p, ArrayList<String> alreadyFoundPairs) {
        ArrayList<Path> result = new ArrayList<Path>();
        Vertice currentSrc = p.dst();
        Vertice predecessor = p.get(p.size() - 2);
        String inputPort = currentSrc.edge(predecessor).color();
        for (Edge e : currentSrc.getAdj()) {
            Vertice dst = e.destination();
            // going back
            if (dst == predecessor)
                continue;
            // going to forbidden direction
            if (currentSrc.getRestriction(inputPort).contains(currentSrc.edge(dst).color()))
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
        Collections.sort(paths, new Path.SrcDst());
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

    private String pairDescriptor(Vertice src, Vertice dst) {
        return src.getNome() + ":" + dst.getNome();
    }
}
